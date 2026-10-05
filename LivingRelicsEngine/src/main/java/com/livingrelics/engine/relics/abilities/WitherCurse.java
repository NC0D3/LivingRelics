package com.livingrelics.engine.relics.abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.Registries; // IMPORTANTE: Para acceder a la clave del registro maestro
import net.minecraft.resources.ResourceKey;

public class WitherCurse implements RelicAbility {

        private static final float SKULL_DAMAGE = 60.0F;

        @Override
        public void execute(Player player, LivingEntity target) {

        }

        public boolean canHit(Player player, LivingEntity target) {

                if (target == player) {
                        return false;
                }

                if (!target.isAlive()) {
                        return false;
                }
                /*
                 * Hostiles normales
                 */
                if (target instanceof Enemy) {
                        return true;
                }
                /*
                 * Neutrales que están atacando
                 */
                if (target instanceof Mob mob) {
                        return mob.getTarget() == player;
                }
                return false;
        }

        public void applyWither(LivingEntity target) {

                target.addEffect(
                                new MobEffectInstance(
                                                MobEffects.WITHER,
                                                20 * 8, // 5 segundos
                                                4 // Wither V
                                ));

                System.out.println(
                                "[WitherSword] Wither applied");
        }

        public void launchSkull(Player player, LivingEntity target) {

                if (!(player.level() instanceof ServerLevel level)) {
                        return;
                }

                Vec3 eye = player.getEyePosition();

                Vec3 direction = target.getEyePosition()
                                .subtract(eye)
                                .normalize();

                Vec3 side = direction
                                .cross(new Vec3(0, 1, 0))
                                .normalize();

                double offset = 0.45;

                for (int i = -1; i <= 1; i++) {

                        WitherSkull skull = (WitherSkull) BuiltInRegistries.ENTITY_TYPE
                                        .getOrThrow(
                                                        ResourceKey.create(
                                                                        Registries.ENTITY_TYPE,
                                                                        Identifier.fromNamespaceAndPath(
                                                                                        "minecraft",
                                                                                        "wither_skull")))
                                        .value()
                                        .create(
                                                        level,
                                                        EntitySpawnReason.TRIGGERED);

                        if (skull == null) {
                                continue;
                        }

                        skull.setOwner(player);

                        trackSkull(level, skull, player);
                        /*
                         * Posición de salida
                         *
                         * izquierda
                         * centro
                         * derecha
                         */
                        Vec3 spawnPos = eye.add(
                                        side.scale(
                                                        offset * i));

                        skull.setPos(
                                        spawnPos);

                        /*
                         * TODOS apuntan al mismo objetivo
                         */
                        skull.shoot(
                                        direction.x,
                                        direction.y,
                                        direction.z,
                                        1.5F,
                                        0);

                        level.addFreshEntity(skull);
                }

                System.out.println(
                                "[WitherSword] Triple skull launched");
        }



        private void trackSkull(
                        ServerLevel level,
                        WitherSkull skull,
                        Player player) {

                net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {

                        if (!skull.isAlive()) {
                                return;
                        }

                        var box = skull.getBoundingBox()
                                        .inflate(1.0);

                        for (LivingEntity living : level.getEntitiesOfClass(
                                        LivingEntity.class,
                                        box,
                                        entity -> entity != player
                                                        && entity.isAlive())) {

                                living.hurt(
                                                player.damageSources().wither(),
                                                SKULL_DAMAGE);

                                living.addEffect(
                                                new MobEffectInstance(
                                                                MobEffects.WITHER,
                                                                20 * 8,
                                                                4));

                                /*
                                 * Explosión visual
                                 */
                                level.sendParticles(
                                                ParticleTypes.EXPLOSION_EMITTER,
                                                skull.getX(),
                                                skull.getY(),
                                                skull.getZ(),
                                                1,
                                                0,
                                                0,
                                                0,
                                                0);

                                /*
                                 * Sonido de impacto
                                 */
                                level.playSound(
                                                null,
                                                skull.getX(),
                                                skull.getY(),
                                                skull.getZ(),
                                                SoundEvents.GENERIC_EXPLODE,
                                                SoundSource.PLAYERS,
                                                2.0F,
                                                1.0F);

                                skull.discard();

                                System.out.println(
                                                "[WitherSword] Skull hit "
                                                                + living.getName().getString());

                                return;
                        }

                });
        }
}