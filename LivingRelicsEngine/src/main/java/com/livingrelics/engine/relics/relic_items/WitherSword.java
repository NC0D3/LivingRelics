package com.livingrelics.engine.relics.relic_items;

import com.livingrelics.engine.relics.RelicDispatcher;
import com.livingrelics.engine.relics.abilities.WitherCurse;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WitherSword extends RelicItem {

    private final WitherCurse curse = new WitherCurse();

    public static void register() {
        RelicDispatcher.register(new WitherSword());
    }

    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath("living_relics","wither_sword");
    }

    @Override
    public void onHit(Player player,LivingEntity target,ItemStack stack) {

        if(!curse.canHit(player,target)){
            return;
        }
        float chance = player.getRandom().nextFloat();
        // 5% Wither Skull
        if(chance <= 0.05f){
            curse.launchSkull(player,target);
            return;
        }
        // 33% Wither Effect
        if(player.getRandom().nextFloat() <= 0.33f){
            curse.applyWither(target);
        }
    }
}