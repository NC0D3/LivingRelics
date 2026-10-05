package com.livingrelics.engine.relics.relic_items;

import java.util.Collections;
import java.util.List;

import com.livingrelics.engine.relics.RelicDispatcher;
import com.livingrelics.engine.relics.abilities.GuardianBeam;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ElderGuardianSword extends RelicItem {
    private final GuardianBeam gBeam = new GuardianBeam();
     private static final double RANGE = 10.0;


    public static void register() {
        RelicDispatcher.register(new ElderGuardianSword());
    }

    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath("living_relics","elder_guardian_sword");
    }

    @Override
    public void onHit(Player player, LivingEntity target, ItemStack stack) {
        ServerLevel level = (ServerLevel) player.level();

        List<Monster> targets = level.getEntitiesOfClass(
                Monster.class,
                player.getBoundingBox().inflate(RANGE),
                entity -> entity.isAlive()
        );

        if (targets.isEmpty()) {
            return;
        }

        Collections.shuffle(targets);

        float chance = player.getRandom().nextFloat();
        if (chance < 0.10F) {
            gBeam.castTriple(player,targets.stream().limit(3).toList());
        } else if (chance < 0.40F) {
            gBeam.castStrong(player,targets.getFirst());
        } else {
            gBeam.castLight(player,targets.getFirst());
        }
    }
}
