package living.relics.item.weapon;

import living.relics.item.ModItems;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.entity.player.Player;

import living.relics.habilities.WitherCurse;

public class WitherSword extends WeaponItem {
    private final WitherCurse curse = new WitherCurse();

    public WitherSword() {

        super(
                WeaponSettings.create()
                        .id("wither_sword")
                        .type(WeaponSettings.Type.SWORD)
                        // Lo que verá el cliente vanilla
                        .vanillaItem(Items.DIAMOND_SWORD)
                        // Combate
                        .attackDamage(12.0F)
                        .attackSpeed(-3.0F)
                        // Durabilidad
                        .durability(2500)
                        // Reparación
                        .repairWith(() -> ModItems.VIVIANITE_CRYSTAL)
                        // Arma de reliquia
                        .relic(() -> ModItems.WITHER_SKULL)
                        // Apariencia
                        .titleColor(TextColor.fromRgb(0x575757))
                        .rarity(Rarity.EPIC)
                        .fireResistant(false)
                        .glint(false)
                        // Encantabilidad
                        .enchantability(18)
                        .build());
    }

    // ============================================================
    // WARDEN ECHO
    // ============================================================

    @Override
    protected void onHit(Player player, LivingEntity target, ItemStack stack) {
        if (!curse.canHit(player, target)) {
            return;
        }
        float chance = player.getRandom().nextFloat();
        // 5% Wither Skull
        if (chance <= 0.1f) {
            curse.launchSkull(player, target);
            return;
        }
        // 33% Wither Effect
        if (player.getRandom().nextFloat() <= 0.33f) {
            curse.applyWither(target);
        }
    }
}