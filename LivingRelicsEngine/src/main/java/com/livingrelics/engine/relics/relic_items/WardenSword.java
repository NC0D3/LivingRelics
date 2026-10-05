package com.livingrelics.engine.relics.relic_items;

import com.livingrelics.engine.relics.RelicDispatcher;
import com.livingrelics.engine.relics.abilities.WardenEcho;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WardenSword extends RelicItem {

    private final WardenEcho echo = new WardenEcho();
    
    public static void register() {
        RelicDispatcher.register(new WardenSword());
    }

    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath("living_relics","warden_sword");
    }

    @Override
    public void onHit(Player player, LivingEntity target, ItemStack stack) {
        if(player.getRandom().nextFloat() <= 0.15f){
            echo.execute(player, target);
        }
    }
}