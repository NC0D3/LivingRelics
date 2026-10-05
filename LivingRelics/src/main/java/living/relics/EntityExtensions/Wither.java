package living.relics.EntityExtensions;

import living.relics.item.ModItems;

public class Wither extends BossBar {

    public Wither() {

        super();

        entity("minecraft:wither")
                .type(Type.MINIBOSS)

                // ====================================================
                // BOSS BAR
                // ====================================================

                .bossBar(false)
                .name("Wither")
                .nameColor("#575757")
                .style(Style.PROGRESS)

                // ====================================================
                // LOOT
                // ====================================================

                .drop(
                        () -> ModItems.WITHER_SKULL,
                        0.05F)

                .build();
    }
}