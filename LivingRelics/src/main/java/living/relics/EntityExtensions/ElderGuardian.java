package living.relics.EntityExtensions;

import living.relics.item.ModItems;

public class ElderGuardian extends BossBar {

    public ElderGuardian() {

        super();

        entity("minecraft:elder_guardian")
                .type(Type.MINIBOSS)

                // ====================================================
                // BOSS BAR
                // ====================================================

                .bossBar(true)
                .name("Elder Guardian")
                .nameColor("#cfc2a1")
                .style(Style.PROGRESS)

                // ====================================================
                // LOOT
                // ====================================================

                .drop(
                        () -> ModItems.ELDER_GUARDIAN_EYE,
                        0.025F)

                .build();
    }
}