package living.relics.item.weapon;

import living.relics.item.ModItems;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

public class VivianiteSword extends WeaponItem {

    public VivianiteSword() {
        super(
                WeaponSettings.create()
                        .id("vivianite_sword")
                        .type(WeaponSettings.Type.SWORD)
                        // Lo que verá el cliente vanilla
                        .vanillaItem(Items.DIAMOND_SWORD)
                        // Combate
                        .attackDamage(8.0F)
                        .attackSpeed(-2.4F)
                        // Durabilidad
                        .durability(2500)
                        // Reparación
                        .repairWith(() -> ModItems.VIVIANITE_CRYSTAL)
                        // Es un arma de material
                        .material(() -> ModItems.VIVIANITE_CRYSTAL)
                        // Apariencia
                        .titleColor(TextColor.fromRgb(0xFFFFFF))
                        .rarity(Rarity.COMMON)
                        .fireResistant(false)
                        .glint(false)
                        // Encantabilidad
                        .enchantability(18)
                        .build()
        );
    }
}