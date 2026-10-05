package com.livingrelics.engine.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class VivianiteWorldgen {

    private VivianiteWorldgen() {}

    private static final ResourceKey<PlacedFeature> VIVIANITE_ORE =
            ResourceKey.create(
                    Registries.PLACED_FEATURE,
                    Identifier.fromNamespaceAndPath(
                            "living_relics",
                            "vivianite_ore"
                    )
            );


    public static void register() {
        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(
                        Biomes.LUSH_CAVES
                ),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                VIVIANITE_ORE
        );


        System.out.println(
                "[Living Relics] Registered Vivianite Ore world generation!"
        );
    }
}