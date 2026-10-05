package living.relics.EntityExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import java.util.Optional;
import net.minecraft.core.Holder;

public class BossBar {

    // ============================================================
    // TYPES
    // ============================================================

    public enum Type {

        NORMAL,
        MINIBOSS,
        BOSS

    }

    // ============================================================
    // BOSS BAR STYLES
    // ============================================================

    public enum Style {

        PROGRESS,
        NOTCHED_6,
        NOTCHED_10,
        NOTCHED_12,
        NOTCHED_20

    }

    // ============================================================
    // DROP
    // ============================================================

    public static class Drop {

        private final Supplier<Item> item;
        private final float chance;

        public Drop(Supplier<Item> item,float chance) {
            this.item = item;
            this.chance = chance;
        }

        public Item getItem() {

            return item.get();

        }

        public float getChance() {

            return chance;

        }

    }

    // ============================================================
    // DATA
    // ============================================================

    private EntityType<?> entity;

    private Type type = Type.NORMAL;

    // ============================================================
    // BOSS BAR
    // ============================================================

    private boolean bossBarEnabled = false;

    private String bossBarName = "";

    private String bossBarNameColor = "#FFFFFF";

    private Style bossBarStyle = Style.PROGRESS;

    // ============================================================
    // LOOT
    // ============================================================

    private final List<Drop> drops = new ArrayList<>();

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public BossBar() {

    }

    // ============================================================
    // ENTITY
    // ============================================================

    /**
     * Define la entidad asociada.
     *
     * Ejemplo:
     *
     * entity("minecraft:warden")
     */
    protected BossBar entity(String entityId) {

        Identifier identifier = Identifier.parse(entityId);

        Optional<Holder.Reference<EntityType<?>>> holder = BuiltInRegistries.ENTITY_TYPE.get(identifier);

        if (holder.isEmpty()) {

            throw new IllegalArgumentException(
                    "Unknown entity type: " + entityId);

        }

        this.entity = holder.get().value();

        return this;
    }

    /**
     * También permitimos pasar directamente
     * un EntityType por si posteriormente
     * lo necesitamos.
     */
    protected BossBar entity(EntityType<?> entity) {

        if (entity == null) {

            throw new IllegalArgumentException(
                    "Entity type cannot be null.");

        }

        this.entity = entity;

        return this;

    }

    // ============================================================
    // TYPE
    // ============================================================

    protected BossBar type(Type type) {

        if (type == null) {

            throw new IllegalArgumentException(
                    "BossBar type cannot be null.");

        }

        this.type = type;

        return this;

    }

    // ============================================================
    // BOSS BAR ENABLED
    // ============================================================

    protected BossBar bossBar(boolean enabled) {

        this.bossBarEnabled = enabled;

        return this;

    }

    // ============================================================
    // NAME
    // ============================================================

    protected BossBar name(String name) {

        this.bossBarName = name;

        return this;

    }

    // ============================================================
    // NAME COLOR
    // ============================================================

    protected BossBar nameColor(String color) {

        this.bossBarNameColor = color;

        return this;

    }

    // ============================================================
    // STYLE
    // ============================================================

    protected BossBar style(Style style) {

        if (style == null) {

            throw new IllegalArgumentException(
                    "BossBar style cannot be null.");

        }

        this.bossBarStyle = style;

        return this;

    }

    // ============================================================
    // LOOT
    // ============================================================

    /**
     * Agrega un drop.
     *
     * chance:
     *
     * 1.0F = 100%
     * 0.5F = 50%
     * 0.05F = 5%
     */
    protected BossBar drop(
            Supplier<Item> item,
            float chance) {

        if (item == null) {

            throw new IllegalArgumentException(
                    "Drop item cannot be null.");

        }

        if (chance < 0.0F || chance > 1.0F) {

            throw new IllegalArgumentException(
                    "Drop chance must be between 0.0 and 1.0.");

        }

        drops.add(
                new Drop(
                        item,
                        chance));

        return this;

    }

    // ============================================================
    // BUILD
    // ============================================================

    /**
     * Finaliza la configuración.
     *
     * La instancia queda lista para ser
     * registrada por el BossBarManager.
     */
    protected BossBar build() {

        validate();

        BossBarManager.register(this);

        return this;

    }

    // ============================================================
    // VALIDATION
    // ============================================================

    private void validate() {

        if (entity == null) {

            throw new IllegalStateException(
                    "BossBar entity has not been specified.");

        }

        if (bossBarEnabled) {

            if (bossBarName == null ||
                    bossBarName.isBlank()) {

                throw new IllegalStateException(
                        "BossBar name has not been specified.");

            }

            if (bossBarNameColor == null ||
                    bossBarNameColor.isBlank()) {

                throw new IllegalStateException(
                        "BossBar name color has not been specified.");

            }

        }

    }

    // ============================================================
    // GETTERS
    // ============================================================

    public EntityType<?> getEntity() {

        return entity;

    }

    public Type getType() {

        return type;

    }

    public boolean hasBossBar() {

        return bossBarEnabled;

    }

    public String getBossBarName() {

        return bossBarName;

    }

    public String getBossBarNameColor() {

        return bossBarNameColor;

    }

    public Style getBossBarStyle() {

        return bossBarStyle;

    }

    public List<Drop> getDrops() {

        return drops;

    }

    // ============================================================
    // HELPERS
    // ============================================================

    public boolean hasLoot() {

        return !drops.isEmpty();

    }

}