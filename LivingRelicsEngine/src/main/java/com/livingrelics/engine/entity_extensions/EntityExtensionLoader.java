package com.livingrelics.engine.entity_extensions;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.Identifier;

import java.io.Reader;
import java.util.Map;

public class EntityExtensionLoader {
    public static void load(ResourceManager manager){
        System.out.println("[Living Relics] Searching entity extensions...");
        Map<Identifier, Resource> resources = manager.listResources("entity_extensions",path -> path.toString().endsWith(".json"));
        resources.forEach((id, resource)->{
                try{
                        Reader reader = resource.openAsReader();
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        EntityExtension extension = new EntityExtension(json);
                        EntityExtensionManager.register(extension);
                        System.out.println("[Living Relics] Loaded extension: " + extension.getEntity());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        );
    }
}