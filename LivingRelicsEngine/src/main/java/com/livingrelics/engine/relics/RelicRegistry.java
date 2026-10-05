package com.livingrelics.engine.relics;

import com.livingrelics.engine.relics.relic_items.ElderGuardianSword;
import com.livingrelics.engine.relics.relic_items.WardenSword;
import com.livingrelics.engine.relics.relic_items.WitherSword;

public final class RelicRegistry {

    private RelicRegistry() {}

    public static void register() {

        WardenSword.register();
        ElderGuardianSword.register();
        WitherSword.register();

        // InfernalSword.register();
        // GuardianBow.register();
        // WardenShield.register();

    }

}