package living.relics.item.weapon;

import living.relics.LivingRelics;

import eu.pb4.polymer.core.api.item.VanillaModeledPolymerItem;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.core.HolderSet;

public class WeaponItem extends Item
                implements VanillaModeledPolymerItem {

        // ============================================================
        // SETTINGS
        // ============================================================

        protected final WeaponSettings settings;

        // ============================================================
        // CONSTRUCTOR
        // ============================================================

        public WeaponItem(WeaponSettings settings) {

                super(createProperties(settings));
                this.settings = settings;
                registerItem();
                registerEvents();

        }

        // ============================================================
        // PROPERTIES
        // ============================================================

        private static Properties createProperties(WeaponSettings settings) {

                Identifier id = Identifier.fromNamespaceAndPath(
                                LivingRelics.MOD_ID,
                                settings.getId());

                ResourceKey<Item> itemKey = ResourceKey.create(
                                BuiltInRegistries.ITEM.key(),
                                id);

                Properties properties = new Properties()
                                .setId(itemKey)
                                .stacksTo(1)
                                .durability(settings.getDurability())
                                .rarity(settings.getRarity());

                /*
                 * ========================================================
                 * VANILLA TOOL BEHAVIOR
                 * ========================================================
                 */

                applyVanillaType(properties, settings);

                properties.durability(settings.getDurability());
                /*
                 * ========================================================
                 * COMMON SETTINGS
                 * ========================================================
                 */

                applyCommonProperties(properties, settings);

                return properties;

        }

        // ============================================================
        // VANILLA TYPE
        // ============================================================

        private static void applyVanillaType(

                        Properties properties,

                        WeaponSettings settings

        ) {

                ToolMaterial material = getToolMaterial(
                                settings.getVanillaItem());

                switch (settings.getType()) {

                        case SWORD -> {

                                properties.sword(

                                                material,

                                                settings.getAttackDamage(),

                                                settings.getAttackSpeed()

                                );

                        }

                        case PICKAXE -> {

                                properties.pickaxe(

                                                material,

                                                settings.getAttackDamage(),

                                                settings.getAttackSpeed()

                                );

                        }

                        /*
                         * Estas tres también pueden utilizar las
                         * propiedades vanilla correspondientes.
                         *
                         * Sus clases vanilla tienen constructores
                         * especiales, así que posteriormente podemos
                         * convertirlas en clases internas si necesitamos
                         * comportamiento de clase específico.
                         */

                        case AXE -> {

                                properties.axe(

                                                material,

                                                settings.getAttackDamage(),

                                                settings.getAttackSpeed()

                                );

                        }

                        case SHOVEL -> {

                                properties.shovel(

                                                material,

                                                settings.getAttackDamage(),

                                                settings.getAttackSpeed()

                                );

                        }

                        case HOE -> {

                                properties.hoe(

                                                material,

                                                settings.getAttackDamage(),

                                                settings.getAttackSpeed()

                                );

                        }

                        /*
                         * Armas que todavía no tienen comportamiento
                         * vanilla asignado.
                         */

                        case HAMMER,
                                        DAGGER,
                                        SPEAR,
                                        BOW,
                                        CROSSBOW,
                                        STAFF ->
                                {

                                }

                }

        }

        // ============================================================
        // COMMON PROPERTIES
        // ============================================================

        private static void applyCommonProperties(Properties properties, WeaponSettings settings) {
                /*
                 * FIRE RESISTANT
                 */
                if (settings.isFireResistant()) {
                        properties.fireResistant();
                }
                /*
                 * GLINT
                 */
                if (settings.hasGlint()) {
                        properties.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                }
                /*
                 * NAME
                 */
                properties.component(
                                DataComponents.CUSTOM_NAME,
                                Component.translatable(
                                                "item."
                                                                + LivingRelics.MOD_ID
                                                                + "."
                                                                + settings.getId())
                                                .setStyle(
                                                                Style.EMPTY
                                                                                .withColor(
                                                                                                settings.getTitleColor())
                                                                                .withItalic(false)));

                /*
                 * ========================================================
                 * REPAIR ITEM
                 * ========================================================
                 */

                if (settings.getRepairItem() != null) {
                        Item repairItem = settings.getRepairItem().get();
                        HolderSet<Item> repairItems = HolderSet.direct(BuiltInRegistries.ITEM.wrapAsHolder(repairItem));
                        properties.component(DataComponents.REPAIRABLE, new Repairable(repairItems));
                }

                /*
                 * ========================================================
                 * ENCHANTING
                 * ========================================================
                 */

                if (settings.getEnchantability() > 0) {
                        properties.component(DataComponents.ENCHANTABLE, new Enchantable(settings.getEnchantability()));
                }
        }

        // ============================================================
        // REGISTER
        // ============================================================

        private void registerItem() {

                Registry.register(

                                BuiltInRegistries.ITEM,

                                Identifier.fromNamespaceAndPath(

                                                LivingRelics.MOD_ID,

                                                settings.getId()

                                ),

                                this

                );

        }

        // ============================================================
        // POLYMER
        // ============================================================

        @Override
        public Item getPolymerItem(

                        ItemStack stack,

                        PacketContext context

        ) {

                return settings.getVanillaItem();

        }

        @Override
        public Identifier getPolymerItemModel(

                        ItemStack stack,

                        PacketContext context,

                        HolderLookup.Provider lookup

        ) {

                return getCurrentModel(stack);

        }

        // ============================================================
        // MODEL
        // ============================================================

        protected Identifier getCurrentModel(

                        ItemStack stack

        ) {

                return Identifier.fromNamespaceAndPath(

                                LivingRelics.MOD_ID,

                                settings.getId()

                );

        }

        // ============================================================
        // EVENTS
        // ============================================================
        // ============================================================
        // EVENT SYSTEM
        // ============================================================

        private static boolean eventsRegistered = false;

        protected static void registerEvents() {

                if (eventsRegistered) {
                        return;
                }

                eventsRegistered = true;

                AttackEntityCallback.EVENT.register(
                                (player, level, hand, entity, hitResult) -> {

                                        ItemStack stack = player.getItemInHand(hand);

                                        if (!(stack.getItem() instanceof WeaponItem weapon)) {
                                                return InteractionResult.PASS;
                                        }

                                        if (!(entity instanceof LivingEntity target)) {
                                                return InteractionResult.PASS;
                                        }

                                        /*
                                         * Ejecutamos el hook del arma.
                                         *
                                         * Esto NO cancela el ataque vanilla.
                                         */
                                        weapon.onHit(
                                                        player,
                                                        target,
                                                        stack);

                                        return InteractionResult.PASS;
                                });
        }

        protected void onHit(

                        Player player,

                        LivingEntity target,

                        ItemStack stack

        ) {

        }

        protected void onKill(

                        Player player,

                        LivingEntity target,

                        ItemStack stack

        ) {

        }

        protected void onRightClick(

                        Player player,

                        ItemStack stack

        ) {

        }

        protected void onBlockBreak(

                        Player player,

                        ItemStack stack

        ) {

        }

        protected void onInventoryTick(

                        ItemStack stack

        ) {

        }

        // ============================================================
        // TOOL MATERIAL
        // ============================================================

        private static ToolMaterial getToolMaterial(

                        Item vanillaItem

        ) {

                /*
                 * NETHERITE
                 */

                if (vanillaItem == Items.NETHERITE_SWORD
                                || vanillaItem == Items.NETHERITE_PICKAXE
                                || vanillaItem == Items.NETHERITE_AXE
                                || vanillaItem == Items.NETHERITE_SHOVEL
                                || vanillaItem == Items.NETHERITE_HOE) {

                        return ToolMaterial.NETHERITE;

                }

                /*
                 * DIAMOND
                 */

                if (vanillaItem == Items.DIAMOND_SWORD
                                || vanillaItem == Items.DIAMOND_PICKAXE
                                || vanillaItem == Items.DIAMOND_AXE
                                || vanillaItem == Items.DIAMOND_SHOVEL
                                || vanillaItem == Items.DIAMOND_HOE) {

                        return ToolMaterial.DIAMOND;

                }

                /*
                 * IRON
                 */

                if (vanillaItem == Items.IRON_SWORD
                                || vanillaItem == Items.IRON_PICKAXE
                                || vanillaItem == Items.IRON_AXE
                                || vanillaItem == Items.IRON_SHOVEL
                                || vanillaItem == Items.IRON_HOE) {

                        return ToolMaterial.IRON;

                }

                /*
                 * GOLD
                 */

                if (vanillaItem == Items.GOLDEN_SWORD
                                || vanillaItem == Items.GOLDEN_PICKAXE
                                || vanillaItem == Items.GOLDEN_AXE
                                || vanillaItem == Items.GOLDEN_SHOVEL
                                || vanillaItem == Items.GOLDEN_HOE) {

                        return ToolMaterial.GOLD;

                }

                /*
                 * STONE
                 */

                if (vanillaItem == Items.STONE_SWORD
                                || vanillaItem == Items.STONE_PICKAXE
                                || vanillaItem == Items.STONE_AXE
                                || vanillaItem == Items.STONE_SHOVEL
                                || vanillaItem == Items.STONE_HOE) {

                        return ToolMaterial.STONE;

                }

                /*
                 * DEFAULT
                 */

                return ToolMaterial.WOOD;

        }

}