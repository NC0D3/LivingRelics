package living.relics.item.weapon;

import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

public class WeaponSettings {

    // ============================================================
    // WEAPON TYPES
    // ============================================================

    public enum Type {

        SWORD,
        AXE,
        PICKAXE,
        SHOVEL,
        HOE,

        HAMMER,
        DAGGER,
        SPEAR,

        BOW,
        CROSSBOW,

        STAFF

    }

    // ============================================================
    // CATEGORY
    // ============================================================

    public enum Category {

        MATERIAL,
        RELIC

    }

    // ============================================================
    // DATA
    // ============================================================

    private String id;

    private Type type = Type.SWORD;

    private Item vanillaItem = Items.IRON_SWORD;

    private float attackDamage = 0.0F;

    private float attackSpeed = 0.0F;

    private int durability = 250;

    private Supplier<Item> repairItem;

    private Supplier<Item> relicItem;

    private Category category;

    private TextColor titleColor = TextColor.fromRgb(0xFFFFFF);

    private boolean fireResistant = false;

    private boolean glint = false;

    private Rarity rarity = Rarity.COMMON;

    private int enchantability = 0;

    // ============================================================
    // FACTORY
    // ============================================================

    public static WeaponSettings create() {

        return new WeaponSettings();

    }

    private WeaponSettings() {
    }

    // ============================================================
    // BUILDER
    // ============================================================

    public WeaponSettings id(String id) {

        this.id = id;
        return this;

    }

    public WeaponSettings type(Type type) {

        this.type = type;
        return this;

    }

    public WeaponSettings vanillaItem(Item vanillaItem) {

        this.vanillaItem = vanillaItem;
        return this;

    }

    public WeaponSettings durability(int durability) {

        this.durability = durability;
        return this;

    }

    public WeaponSettings attackDamage(float attackDamage) {

        this.attackDamage = attackDamage;
        return this;

    }

    public WeaponSettings attackSpeed(float attackSpeed) {

        this.attackSpeed = attackSpeed;
        return this;

    }

    public WeaponSettings repairWith(Supplier<Item> repairItem) {

        this.repairItem = repairItem;
        return this;

    }
    //no
    public WeaponSettings material(Supplier<Item> material) {

        if (this.category == Category.RELIC) {

            throw new IllegalStateException(
                    "A relic weapon cannot also be a material weapon.");

        }

        this.category = Category.MATERIAL;

        return this;

    }

    public WeaponSettings relic(Supplier<Item> relic) {

        if (this.category == Category.MATERIAL) {

            throw new IllegalStateException(
                    "A material weapon cannot also be a relic weapon.");

        }

        this.category = Category.RELIC;
        this.relicItem = relic;

        return this;

    }

    public WeaponSettings titleColor(TextColor color) {

        this.titleColor = color;
        return this;

    }

    public WeaponSettings fireResistant(boolean value) {

        this.fireResistant = value;
        return this;

    }

    public WeaponSettings glint(boolean value) {

        this.glint = value;
        return this;

    }

    public WeaponSettings rarity(Rarity rarity) {

        this.rarity = rarity;
        return this;

    }

    public WeaponSettings enchantability(int enchantability) {

        this.enchantability = enchantability;
        return this;

    }

    // ============================================================
    // VALIDATION
    // ============================================================

    public WeaponSettings build() {

        Objects.requireNonNull(
                id,
                "Weapon id cannot be null.");

        Objects.requireNonNull(
                type,
                "Weapon type cannot be null.");

        Objects.requireNonNull(
                vanillaItem,
                "Vanilla item cannot be null.");

        Objects.requireNonNull(
                repairItem,
                "Repair item cannot be null.");

        Objects.requireNonNull(
                category,
                "Weapon category has not been specified. Use material(...) or relic(...).");

        if (category == Category.RELIC && relicItem == null) {

            throw new IllegalStateException(
                    "Relic weapons require relic(...).");

        }

        return this;

    }

    // ============================================================
    // GETTERS
    // ============================================================

    public String getId() {

        return id;

    }

    public Type getType() {

        return type;

    }

    public Item getVanillaItem() {

        return vanillaItem;

    }

    public float getAttackDamage() {

        return attackDamage;

    }

    public float getAttackSpeed() {

        return attackSpeed;

    }

    public int getDurability() {

        return durability;

    }

    public Supplier<Item> getRepairItem() {

        return repairItem;

    }

    public Supplier<Item> getRelicItem() {

        return relicItem;

    }

    public Category getCategory() {

        return category;

    }

    public TextColor getTitleColor() {

        return titleColor;

    }

    public boolean isFireResistant() {

        return fireResistant;

    }

    public boolean hasGlint() {

        return glint;

    }

    public Rarity getRarity() {

        return rarity;

    }

    public int getEnchantability() {

        return enchantability;

    }

    // ============================================================
    // HELPERS
    // ============================================================

    public boolean isRelic() {

        return category == Category.RELIC;

    }

    public boolean isMaterialWeapon() {

        return category == Category.MATERIAL;

    }

}