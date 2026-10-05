import json
import sys
import os
import uuid
import re

def transformar_meta(archivo_entrada,archivo_salida):
    # Leemos el archivo que pasaste como primer parámetro
    with open(archivo_entrada, 'r', encoding='utf-8') as f:
        blueprint_data = json.load(f)
    # Rescatamos tu UUID
    uuid_original = blueprint_data["meta"]["uuid"]
    # Magia para el namespace: 
    # Si el archivo de salida es "jellyfish.ajmodel", esto extrae solo "jellyfish"
    nombre_base = os.path.basename(archivo_salida)
    namespace_dinamico = os.path.splitext(nombre_base)[0]
    # META
    nuevo_modelo = {}
    nuevo_modelo["meta"] = {
        "format": "animated_java/ajmodel",
        "format_version": "0.4.8",
        "uuid": uuid_original
    }
    # variants
    variantes_convertidas = []
    if "variants" in blueprint_data:
        # Extraemos las variantes que vienen en el blueprint
        variantes_originales = blueprint_data["variants"]
        # Iteramos por cada variante (ej. "default", "apple")
        for clave, datos_var in variantes_originales.items():
            # Saltamos la clave "list" porque no es una variante en sí
            if clave == "list":
                continue
            # Armamos el nuevo bloque de variante adaptando los nombres
            nueva_var = {
                "name": datos_var.get("name", clave),
                "textureMap": datos_var.get("texture_map", {}),
                "uuid": datos_var.get("uuid", str(uuid.uuid4())), # Rescatamos su UUID si lo tiene
                "boneConfig": {},
                "affectedBonesIsAWhitelist": False,
                "affectedBones": []
            }
            # Si la original tenía "is_default", se lo pasamos como "default"
            if datos_var.get("is_default") is True:
                nueva_var["default"] = True
                
            variantes_convertidas.append(nueva_var)
    else:
        # Si por alguna razón el blueprint no trae variantes, armamos la default de emergencia
        variantes_convertidas.append({
            "name": "default",
            "textureMap": {},
            "uuid": str(uuid.uuid4()),
            "boneConfig": {},
            "default": True,
            "affectedBonesIsAWhitelist": False,
            "affectedBones": []
        })
    # ANIMATED_JAVA
    nuevo_modelo["animated_java"] = {
        "settings": {
            "project_namespace": namespace_dinamico,  # <-- Aquí se pone "gato" o "jellyfish" dinámicamente
            "project_resolution": [16, 16],
            "target_minecraft_version": "1.20+",
            "rig_item": "minecraft:leather_horse_armor",
            "rig_item_model": "",
            "rig_export_folder": "",
            "texture_export_folder": "",
            "enable_advanced_resource_pack_settings": False,
            "resource_pack_mcmeta": "",
            "verbose": True,
            "exporter": "animated_java:json_exporter"
        },
        "exporter_settings": {
            "animated_java:datapack_exporter": {
                "datapack_mcmeta": "",
                "outdated_rig_warning": True,
                "root_entity_nbt": "{}",
                "use_component_system": False,
                "include_variant_summon_functions": True,
                "include_apply_variant_functions": True,
                "include_uninstall_function": True,
                "include_pause_all_animations_function": True,
                "include_remove_rigs_function": True,
                "include_remove_all_function": True,
                "include_on_load_function_tags": True,
                "include_on_tick_function_tags": True,
                "include_on_summon_function_tags": True,
                "include_on_remove_function_tags": True
            },
            "animated_java:json_exporter": {
                "output_file": ""
            }
        },
        "variants": variantes_convertidas
    }
    # RESOLUTION
    nuevo_modelo["resolution"] = {
        "width": 64,
		"height": 64
    }
    # ELEMENTS
    mapa_texturas = {}
    if "textures" in blueprint_data:
        for textura in blueprint_data["textures"]:
            # Buscamos el uuid y el id. El id viene como texto ("0"), lo pasamos a entero (0)
            if "uuid" in textura and "id" in textura:
                mapa_texturas[textura["uuid"]] = int(textura["id"])
    
    elementos_convertidos = []
    if "elements" in blueprint_data:
        orden_estricto = [
            "name", "box_uv", "rescale", "locked", "light_emission", 
            "render_order", "allow_mirror_modeling", "from", "to", 
            "autouv", "color","rotation", "inflate", "origin", "uv_offset", "faces", "type", "uuid"
        ]
        for elemento in blueprint_data["elements"]:
            nuevo_elemento = {}
            # Filtramos y ordenamos
            for clave in orden_estricto:
                if clave in elemento:
                    nuevo_elemento[clave] = elemento[clave]
            # Reemplazamos los UUIDs feos por los IDs limpios en las caras
            if "faces" in nuevo_elemento:
                for cara, datos_cara in nuevo_elemento["faces"].items():
                    if "texture" in datos_cara:
                        textura_val = datos_cara["texture"]
                        # Si el UUID está en nuestro diccionario traductor, lo cambiamos por el número
                        if textura_val in mapa_texturas:
                            datos_cara["texture"] = mapa_texturas[textura_val] 
            elementos_convertidos.append(nuevo_elemento)
    nuevo_modelo["elements"] = elementos_convertidos
    # OUTLINER
    diccionario_grupos = {}
    if "groups" in blueprint_data:
        for grupo in blueprint_data["groups"]:
            if "uuid" in grupo:
                diccionario_grupos[grupo["uuid"]] = grupo
    # Lógica interna para procesar el árbol jerárquico respetando el formato ajmodel
    def procesar_nodos_outliner(nodos_blueprint):
        nodos_convertidos = []
        for nodo in nodos_blueprint:
            # Si el nodo es solo un texto (string), es el UUID de un "element", lo pasamos directo
            if isinstance(nodo, str):
                nodos_convertidos.append(nodo)
            # Si es un diccionario, es un grupo/carpeta, hay que formatearlo
            elif isinstance(nodo, dict) and "uuid" in nodo:
                uuid_nodo = nodo["uuid"]
                # Buscamos sus propiedades en el diccionario de grupos (si no existe, usamos dict vacío)
                datos_grupo = diccionario_grupos.get(uuid_nodo, {})
                
                # Construimos el nodo en el ORDEN EXACTO del ajmodel
                nuevo_nodo = {
                    "name": datos_grupo.get("name", "grupo_desconocido"),
                    "origin": datos_grupo.get("origin", [0, 0, 0]),
                    "color": datos_grupo.get("color", 0),
                    "nbt": "{}",  # Esta propiedad es exclusiva de ajmodel, la inicializamos
                    "uuid": uuid_nodo,
                    "export": datos_grupo.get("export", True),
                    "mirror_uv": datos_grupo.get("mirror_uv", False),
                    "isOpen": nodo.get("isOpen", True), # El isOpen viene del outliner, no del group
                    "locked": datos_grupo.get("locked", False),
                    "visibility": datos_grupo.get("visibility", True),
                    "autouv": datos_grupo.get("autouv", 0),
                }
                
                # Si tiene hijos, llamamos a esta misma lógica para procesarlos hacia adentro
                if "children" in nodo:
                    nuevo_nodo["children"] = procesar_nodos_outliner(nodo["children"])
                else:
                    nuevo_nodo["children"] = []
                    
                nodos_convertidos.append(nuevo_nodo)
                
        return nodos_convertidos
    # Aplicamos la lógica y guardamos el outliner final
    if "outliner" in blueprint_data:
        nuevo_modelo["outliner"] = procesar_nodos_outliner(blueprint_data["outliner"])
    else:
        nuevo_modelo["outliner"] = []

    # TEXTURES
    texturas_convertidas = []
    if "textures" in blueprint_data:
        orden_estricto_texturas = [
            "path", "name", "folder", "namespace", "id", "group", 
            "width", "height", "uv_width", "uv_height", "particle", 
            "use_as_default", "layers_enabled", "sync_to_project", 
            "render_mode", "render_sides", "frame_time", "frame_order_type", 
            "frame_order", "frame_interpolate", "visible", "internal", 
            "saved", "uuid", "source", "image_data", "mode"
        ]
        
        for textura in blueprint_data["textures"]:
            nueva_textura = {}
            
            # Preparamos los datos exclusivos de ajmodel antes de filtrar
            if "source" in textura:
                textura["image_data"] = textura["source"]
            textura["mode"] = "bitmap"
            
            # Filtramos y ordenamos
            for clave in orden_estricto_texturas:
                if clave in textura:
                    nueva_textura[clave] = textura[clave]
                    if clave == "name":
                        nueva_textura["name"] = namespace_dinamico
                    
            texturas_convertidas.append(nueva_textura)
            
    nuevo_modelo["textures"] = texturas_convertidas
    # ANIMATIONS
    animaciones_convertidas = []
    if "animations" in blueprint_data:
        for anim in blueprint_data["animations"]:
            animadores_limpios = {}
            
            if "animators" in anim:
                for bone_uuid, animator_data in anim["animators"].items():
                    # REGLA DE ORO: Si el animador no tiene keyframes o está vacía la lista, lo saltamos (adiós estáticos)
                    if "keyframes" not in animator_data or not animator_data["keyframes"]:
                        continue
                    
                    # Construimos el animador limpio solo con lo que pide el ajmodel
                    animador_limpio = {
                        "name": animator_data.get("name", "hueso"),
                        "type": animator_data.get("type", "bone"),
                        "keyframes": []
                    }
                    
                    # Limpiamos los keyframes individuales
                    for kf in animator_data["keyframes"]:
                        kf_limpio = {
                            "channel": kf.get("channel", "rotation"),
                            "data_points": kf.get("data_points", []),
                            "uuid": kf.get("uuid", str(uuid.uuid4())),
                            "time": kf.get("time", 0),
                            "color": kf.get("color", -1),
                            "interpolation": kf.get("interpolation", "linear")
                        }
                        animador_limpio["keyframes"].append(kf_limpio)
                        
                    animadores_limpios[bone_uuid] = animador_limpio

            # Construcción de la animación adaptada a ajmodel con valores por defecto
            nueva_anim = {
                "uuid": anim.get("uuid", str(uuid.uuid4())),
                "name": anim.get("name", "animation"),
                "loop": anim.get("loop", "loop"),
                "override": anim.get("override", False),
                "length": anim.get("length", 1),
                "snapping": anim.get("snapping", 20),
                "selected": anim.get("selected", False),
                "anim_time_update": anim.get("anim_time_update", ""),
                "blend_weight": anim.get("blend_weight", ""),
                "start_delay": anim.get("start_delay", ""),
                "loop_delay": anim.get("loop_delay", ""),
                "affected_bones": anim.get("affected_bones", []),
                "affected_bones_is_a_whitelist": anim.get("affected_bones_is_a_whitelist", False),
                "animators": animadores_limpios
            }
            
            animaciones_convertidas.append(nueva_anim)
            
    nuevo_modelo["animations"] = animaciones_convertidas





    return nuevo_modelo

# --- Ejecución ---
if __name__ == "__main__":
    # Verificamos que hayas puesto exactamente 3 palabras en la consola:
    # [0] conversor.py, [1] entrada.ajblueprint, [2] salida.ajmodel
    if len(sys.argv) != 3:
        print("Uso incorrecto. Ejecútalo así:")
        print("python conversor.py <archivo_entrada.ajblueprint> <archivo_salida.ajmodel>")
        sys.exit(1)
        
    # Guardamos los nombres que pusiste en la consola en variables
    archivo_entrada = sys.argv[1]
    archivo_salida = sys.argv[2]
    
    try:
        # Hacemos la magia del bloque meta
        resultado = transformar_meta(archivo_entrada, archivo_salida)
        json_texto = json.dumps(resultado, indent='\t')
        # por comas y saltos de línea, y las colapsamos a un formato inline [x, y, z]
        json_texto = re.sub(
            r'\[\s*((?:-?\d+(?:\.\d+)?\s*,\s*)*-?\d+(?:\.\d+)?)\s*\]',
            lambda m: '[' + re.sub(r'\s+', '', m.group(1)).replace(',', ', ') + ']',
            json_texto
        )
        # Guardamos el texto ya corregido y formateado en el archivo
        with open(archivo_salida, 'w', encoding='utf-8') as f:
            f.write(json_texto)
            
        print(f"¡Éxito! Se leyó '{archivo_entrada}' y se guardó el bloque meta en '{archivo_salida}'.")
        
    except FileNotFoundError:
        print(f"Error: No pude encontrar el archivo '{archivo_entrada}'. Revisa que esté en la misma carpeta o escribe la ruta completa.")