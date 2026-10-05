package com.livingrelics.engine.relics.relic_items;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class RelicItem {
    public abstract Identifier getId();

    public void onHit(Player player, LivingEntity target, ItemStack stack) {}

    public void onRightClick(Player player, ItemStack stack) {}

    public void onUseOnBlock(Player player, ItemStack stack) {}

    public void onShieldBlock(Player player, LivingEntity attacker, ItemStack stack) {}

}