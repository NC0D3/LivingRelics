package com.livingrelics.engine.relics;

import java.util.HashMap;
import java.util.Map;

import com.livingrelics.engine.relics.relic_items.RelicItem;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class RelicDispatcher {
    private static final Map<Identifier, RelicItem> RELICS = new HashMap<>();
    private RelicDispatcher() {}

    public static void register(RelicItem relic) {
        System.out.println("Registering relic: " + relic.getId());
        RELICS.put(relic.getId(), relic);
    }

    private static RelicItem get(ItemStack stack) {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return RELICS.get(id);

    }

    public static void onHit(Player player,LivingEntity target,ItemStack stack) {
        RelicItem relic = get(stack);
        if (relic != null) {
            relic.onHit(player, target, stack);
        }
    }

    public static void onRightClick(Player player,ItemStack stack) {
        RelicItem relic = get(stack);
        if (relic != null) {
            relic.onRightClick(player, stack);
        }
    }

    public static void onUseOnBlock(Player player,ItemStack stack) {
        RelicItem relic = get(stack);
        if (relic != null) {
            relic.onUseOnBlock(player, stack);
        }
    }

    public static void onShieldBlock(Player player,LivingEntity attacker,ItemStack stack) {
        RelicItem relic = get(stack);
        if (relic != null) {
            relic.onShieldBlock(player, attacker, stack);
        }
    }
}