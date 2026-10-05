package living.relics.item;

import living.relics.LivingRelics;

import living.relics.item.material.VivianiteCrystal;
import living.relics.item.weapon.VivianiteSword;
import living.relics.item.weapon.WardenSword;
import living.relics.item.weapon.ElderGuardianSword;
import living.relics.item.weapon.WitherSword;

import living.relics.item.relic.WardenCore;
import living.relics.item.relic.ElderGuardianEye;
import living.relics.item.relic.WitherSkull;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.resources.Identifier;

import net.minecraft.world.item.Item;

public class ModItems {

        public static final Item VIVIANITE_CRYSTAL = register("vivianite_crystal",new VivianiteCrystal(new Item.Properties()));
        public static final Item WARDEN_CORE = register("warden_core", new WardenCore(new Item.Properties()));
        public static final Item ELDER_GUARDIAN_EYE = register("elder_guardian_eye", new ElderGuardianEye(new Item.Properties()));
        public static final Item WITHER_SKULL = register("wither_skull", new WitherSkull(new Item.Properties()));
        public static final VivianiteSword VIVIANITE_SWORD = new VivianiteSword();
        public static final WardenSword WARDEN_SWORD = new WardenSword();
        public static final ElderGuardianSword ELDER_GUARDIAN_SWORD = new ElderGuardianSword();
        public static final WitherSword WITHER_SWORD = new WitherSword();

        private static Item register(String name, Item item) {

                return Registry.register(BuiltInRegistries.ITEM,Identifier.fromNamespaceAndPath(LivingRelics.MOD_ID,name),item);
        }

        public static void initialize() {

                LivingRelics.LOGGER.info("Registering Living Relics Items");

        }

}