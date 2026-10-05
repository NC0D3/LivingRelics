package living.relics.EntityExtensions;

import living.relics.item.ModItems;

public class Warden extends BossBar {

    public Warden() {

        super();

        entity("minecraft:warden")
                .type(Type.MINIBOSS)

                // ====================================================
                // BOSS BAR
                // ====================================================

                .bossBar(true)
                .name("Warden")
                .nameColor("#79edd0")
                .style(Style.PROGRESS)

                // ====================================================
                // LOOT
                // ====================================================

                .drop(
                        () -> ModItems.WARDEN_CORE,
                        0.05F)

                .build();
    }
}