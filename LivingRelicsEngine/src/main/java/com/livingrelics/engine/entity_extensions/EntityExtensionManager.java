package com.livingrelics.engine.entity_extensions;


import java.util.HashMap;
import java.util.Map;


public class EntityExtensionManager {
    private static final Map<String, EntityExtension> EXTENSIONS =
            new HashMap<>();

    public static void register(EntityExtension extension){
        EXTENSIONS.put(
                extension.getEntity(),
                extension
        );
    }

    public static EntityExtension get(String entity){
        return EXTENSIONS.get(entity);
    }

    public static boolean exists(String entity){
        return EXTENSIONS.containsKey(entity);
    }
}