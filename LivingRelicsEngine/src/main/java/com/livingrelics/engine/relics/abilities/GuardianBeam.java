package com.livingrelics.engine.relics.abilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class GuardianBeam implements RelicAbility {

    private static final int DURATION = 40;
    private static final Set<UUID> ACTIVE_BEAMS = new HashSet<>();
    private static final Map<UUID, Integer> ACTIVE_BEAM_COUNT = new HashMap<>();

    public void castLight(Player player, LivingEntity target) {
        System.out.println("light beam summoned");
        if (!(player.level() instanceof ServerLevel level))
            return;

        if (target == null || !target.isAlive())
            return;

        if (ACTIVE_BEAMS.contains(player.getUUID())) {
            System.out.println(
                    "[GuardianBeam] Beam already active");
            return;
        }

        ACTIVE_BEAMS.add(player.getUUID());
        BeamTask task = new BeamTask(level, player, target, 0.1f, 10);
        ServerTickEvents.END_SERVER_TICK.register(task);
    }

    public void castStrong(Player player, LivingEntity target) {
        System.out.println("strong beam summoned");
        if (!(player.level() instanceof ServerLevel level))
            return;

        if (target == null || !target.isAlive())
            return;

        if (ACTIVE_BEAMS.contains(player.getUUID())) {
            System.out.println(
                    "[GuardianBeam] Beam already active");
            return;
        }

        ACTIVE_BEAMS.add(player.getUUID());
        BeamTask task = new BeamTask(level, player, target, 0.2f, 20);
        ServerTickEvents.END_SERVER_TICK.register(task);
    }

    public void castTriple(Player player, List<? extends LivingEntity> targets) {
        System.out.println("triple beam summoned");
        if (!(player.level() instanceof ServerLevel level))
            return;

        if (targets == null || targets.isEmpty())
            return;

        if (ACTIVE_BEAMS.contains(player.getUUID())) {
            System.out.println(
                    "[GuardianBeam] Beam already active");
            return;
        }

        ACTIVE_BEAMS.add(player.getUUID());
        ACTIVE_BEAM_COUNT.put(player.getUUID(), targets.size());
        int count = 0;
        for (LivingEntity target : targets) {
            if (target == null || !target.isAlive())
                continue;

            BeamTask task = new BeamTask(level, player, target, 0.2f, 20);
            ServerTickEvents.END_SERVER_TICK.register(task);
            count++;
        }
    }

    @Override
    public void execute(Player player, LivingEntity target) {

        if (!(player.level() instanceof ServerLevel level))
            return;

        if (target == null || !target.isAlive())
            return;

        if (ACTIVE_BEAMS.contains(player.getUUID())) {
            System.out.println(
                    "[GuardianBeam] Beam already active");
            return;
        }

        ACTIVE_BEAMS.add(player.getUUID());
        BeamTask task = new BeamTask(level, player, target, 0.1f, 20);

        ServerTickEvents.END_SERVER_TICK.register(task);

    }

    private static void drawBeam(ServerLevel level, Vec3 start, Vec3 end, int ticks, float size) {

        Vec3 direction = end.subtract(start);

        double distance = direction.length();

        Vec3 forward = direction.normalize();

        // Crear un vector perpendicular al rayo
        Vec3 up = new Vec3(0, 1, 0);

        Vec3 side = forward.cross(up);

        Vec3 vertical = forward.cross(side);

        // Si el rayo es casi vertical, usamos otro eje
        if (side.length() < 0.01) {
            side = forward.cross(new Vec3(1, 0, 0));
        }

        side = side.normalize();

        vertical = vertical.normalize();

        Vec3 step = forward.scale(0.01);

        int particles = (int) (distance / 0.01);

        Vec3 current = start;

        // Parámetros de la onda
        double amplitude = 0.125; // tamaño de la onda
        double frequency = 8; // cantidad de curvas

        for (int i = 0; i < particles; i++) {

            double progress = (double) i / particles;

            /*
             * Onda senoidal
             *
             * progress va de 0 a 1
             * hacemos varias oscilaciones
             * 
             */
            double wave = Math.sin(
                    progress *
                            Math.PI *
                            2 *
                            frequency)
                    *
                    amplitude;

            Vec3 offsetHorizontal = side.scale(wave);

            Vec3 offsetVertical = vertical.scale(wave);

            Vec3 particlePos1 = current.add(offsetHorizontal);
            Vec3 particlePos2 = current.add(offsetVertical);

            double colorProgress = (double) ticks / DURATION;
            int r = 255;
            int g = (int) (255 * (1 - colorProgress));
            int b = (int) (255 * (1 - colorProgress));
            int miColor = (r << 16) | (g << 8) | b;

            DustParticleOptions dust = new DustParticleOptions(miColor, size);

            if(i % 2 == 0){
                level.sendParticles(
                    dust,
                    particlePos1.x,
                    particlePos1.y,
                    particlePos1.z,
                    1,
                    0,
                    0,
                    0,
                    0);
            }
            
            if(i % 2 != 0){
                level.sendParticles(
                        dust,
                        particlePos2.x,
                        particlePos2.y,
                        particlePos2.z,
                        1,
                        0,
                        0,
                        0,
                        0);
            }
            current = current.add(step);
        }
    }

    private static void finishBeam(Player player) {
        UUID id = player.getUUID();
        Integer count = ACTIVE_BEAM_COUNT.get(id);
        if (count == null) {
            ACTIVE_BEAMS.remove(id);
            return;
        }
        count--;
        if (count <= 0) {
            ACTIVE_BEAM_COUNT.remove(id);
            ACTIVE_BEAMS.remove(id);
            System.out.println(
                    "[GuardianBeam] All beams finished");
        } else {
            ACTIVE_BEAM_COUNT.put(id,count);
        }
    }

    private static class BeamTask implements ServerTickEvents.EndTick {
        private final ServerLevel level;
        private final Player player;
        private final LivingEntity target;
        private final float size;
        private final int damage;

        private int ticks = 0;

        private boolean finished = false;

        private boolean damaged = false;

        public BeamTask(ServerLevel level, Player player, LivingEntity target, float size, int damage) {
            this.level = level;
            this.player = player;
            this.target = target;
            this.size = size;
            this.damage = damage;
        }

        @Override
        public void onEndTick(
                net.minecraft.server.MinecraftServer server) {

            if (finished)
                return;

            /*
             * Terminar animación
             */
            if (ticks >= DURATION) {

                if (!damaged && target.isAlive()) {

                    target.hurtServer(
                            level,
                            player.damageSources().playerAttack(player),
                            this.damage);

                    damaged = true;

                }

                finished = true;
                finishBeam(player);
                System.out.println("[GuardianBeam] Beam finished");
                return;

            }

            if (!target.isAlive()) {

                finished = true;
                finishBeam(player);
                System.out.println("[GuardianBeam] Beam finished");
                return;

            }

            Vec3 start = player.getEyePosition();

            Vec3 end = target.getEyePosition();

            if(ticks % 2 == 0){
                GuardianBeam.drawBeam(level, start, end, ticks, this.size);
            }
            ticks++;
        }
    }

}