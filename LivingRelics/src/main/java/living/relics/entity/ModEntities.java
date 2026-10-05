package living.relics.entity;

import living.relics.LivingRelics;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class ModEntities {

    private ModEntities() {
        // Utility class
    }

    // ============================================================
    // GENERIC ENTITY REGISTRATION
    // ============================================================

    public static <T extends Entity> EntityType<T> register(
            String id,
            EntityType.Builder<T> builder) {

        Identifier identifier =
                Identifier.fromNamespaceAndPath(
                        LivingRelics.MOD_ID,
                        id);

        ResourceKey<EntityType<?>> key =
                ResourceKey.create(
                        BuiltInRegistries.ENTITY_TYPE.key(),
                        identifier);

        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                key,
                builder.build(key));
    }

    // ============================================================
    // INITIALIZE
    // ============================================================

    public static void initialize() {

        System.out.println(
                "[Living Relics] Entity registry initialized.");

    }

}