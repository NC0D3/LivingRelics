package living.relics.EntityExtensions;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BossBarEvents {

    // ============================================================
    // REGISTER
    // ============================================================

    public static void register() {

        registerEntityLoad();

        registerServerTick();

        BossBarManager.registerLootEvents();

        System.out.println(
                "[Living Relics] BossBar events registered.");

    }

    // ============================================================
    // ENTITY LOAD
    // ============================================================

    private static void registerEntityLoad() {

        ServerEntityEvents.ENTITY_LOAD.register(
                (entity, world) -> {

                    if (!(entity instanceof LivingEntity living)) {
                        return;
                    }

                    BossBar extension = BossBarManager.get(
                            entity.getType());

                    if (extension == null) {
                        return;
                    }

                    BossBarManager.create(
                            living,
                            extension);

                });

    }

    // ============================================================
    // SERVER TICK
    // ============================================================

    private static void registerServerTick() {

        ServerTickEvents.END_SERVER_TICK.register(
                server -> {

                    for (ServerLevel level : server.getAllLevels()) {

                        for (Entity entity : level.getAllEntities()) {

                            if (!(entity instanceof LivingEntity living)) {
                                continue;
                            }

                            BossBarManager.tick(
                                    living);

                        }

                    }

                });

    }

}