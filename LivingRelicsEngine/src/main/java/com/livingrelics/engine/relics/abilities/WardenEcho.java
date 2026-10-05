package com.livingrelics.engine.relics.abilities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WardenEcho implements RelicAbility {
    @Override
    public void execute(Player player, LivingEntity target) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        Vec3 startPos = player.getEyePosition();
        Vec3 lookDir = player.getLookAngle();

        double maxDistance = 10.0;
        double step = 0.2;
        double radius = 1.5;
        float maxDamage = 60.0F;
        float minDamage = 20.0F;

        Set<LivingEntity> hitTargets = new HashSet<>();

        // Sonido
        level.playSound(null,player.getX(),player.getY(),player.getZ(),SoundEvents.WARDEN_SONIC_BOOM,SoundSource.PLAYERS,3.0F,1.0F);
        for (double d = 0; d <= maxDistance; d += step) {

            Vec3 currentPos = startPos.add(lookDir.scale(d));

            // Partícula
            level.sendParticles(
                    ParticleTypes.SONIC_BOOM,
                    currentPos.x,
                    currentPos.y,
                    currentPos.z,
                    1,
                    0,
                    0,
                    0,
                    0
            );

            AABB searchBox = new AABB(currentPos.add(-radius, -radius, -radius),currentPos.add(radius, radius, radius));

            List<LivingEntity> victims =
                    level.getEntitiesOfClass(
                            LivingEntity.class,
                            searchBox,
                            entity -> {
                                if (entity == player || !entity.isAlive()) {
                                    return false;
                                }
                                // Mobs hostiles
                                if (entity instanceof Enemy) {
                                    return true;
                                }
                                // Mobs neutrales que estén atacando al jugador
                                if (entity instanceof Mob mob) {
                                    return mob.getTarget() == player;
                                }

                                return false;
                            }
                    );

            for (LivingEntity victim : victims) {
                if (!hitTargets.add(victim)) {
                    continue;
                }
                float damage = (float) (maxDamage - ((maxDamage - minDamage) * (d / maxDistance)));
                victim.hurt(level.damageSources().sonicBoom(player),damage);
                Vec3 knockback = lookDir.normalize().scale(0.5);
                victim.push(knockback.x,0.4,knockback.z);
            }
        }
    }

}