package living.relics.entity;

import living.relics.entity.mob.buggle.Buggle;
import living.relics.entity.mob.jellyfish.Jellyfish;

public final class RelicEntityRegistry {

    private RelicEntityRegistry() {
        // Utility class
    }

    // ============================================================
    // INITIALIZE
    // ============================================================

    public static void initialize() {

        System.out.println(
                "[Living Relics] Registering entities...");

        ModEntities.initialize();

        Jellyfish.initialize();
        Buggle.initialize();

        System.out.println(
                "[Living Relics] Entities registered.");

    }

}