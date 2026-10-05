package com.livingrelics.engine.entity_extensions.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.livingrelics.engine.entity_extensions.EntityExtension;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EntityLootManager {

    public static void drop(LivingEntity entity,EntityExtension extension){
        JsonObject loot = extension.getLoot();
        if(loot == null){
            return;
        }
        JsonArray drops = loot.getAsJsonArray("drops");
        if(drops == null){
            return;
        }
        for(var element : drops){
            JsonObject drop = element.getAsJsonObject();
            double chance = drop.get("chance").getAsDouble();
            String itemId = drop.get("item").getAsString();
            var itemReference = BuiltInRegistries.ITEM.get(Identifier.parse(itemId));
            if(itemReference.isEmpty()){
                System.out.println("[Living Relics] Item not found: " + itemId);
                continue;
            }

            Item item = itemReference.get().value();
            if(entity.getRandom().nextDouble() > chance){
                continue;
            }
            ItemStack stack = new ItemStack(item);
            entity.spawnAtLocation((net.minecraft.server.level.ServerLevel) entity.level(),stack);
        }
    }
}