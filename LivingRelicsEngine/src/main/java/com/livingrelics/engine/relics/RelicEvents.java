package com.livingrelics.engine.relics;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;

public final class RelicEvents {

    private RelicEvents() {}

    public static void register() {

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!(entity instanceof LivingEntity living)) {
                return InteractionResult.PASS;
            }
            RelicDispatcher.onHit(player,living,player.getItemInHand(hand));
            return InteractionResult.PASS;
        });
    }
}