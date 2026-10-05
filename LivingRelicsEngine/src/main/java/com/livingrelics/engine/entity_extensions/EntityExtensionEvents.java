package com.livingrelics.engine.entity_extensions;

import com.livingrelics.engine.entity_extensions.bossbar.EntityBossBarManager;
import com.livingrelics.engine.entity_extensions.loot.EntityLootManager;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class EntityExtensionEvents {
    public static void register(){
        ServerEntityEvents.ENTITY_LOAD.register((entity, world)->{
                if(!(entity instanceof LivingEntity living)){
                    return;
                }
                String id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();

                EntityExtension extension = EntityExtensionManager.get(id);

                if(extension == null){
                    return;
                }

                if(extension.hasBossBar()){
                    EntityBossBarManager.create(living,extension);
                }
            }
        );

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            String id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
            EntityExtension extension = EntityExtensionManager.get(id);
            if (extension == null) {
                return;
            }
            EntityLootManager.drop(entity,extension);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerLevel level : server.getAllLevels()) {
                for (Entity entity : level.getAllEntities()) {
                    if (!(entity instanceof LivingEntity living)) {
                        continue;
                    }
                    EntityBossBarManager.tick(living);
                }
            }
        });
    }
}