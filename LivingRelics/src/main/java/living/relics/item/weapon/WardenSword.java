package living.relics.item.weapon;

import living.relics.item.ModItems;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.entity.player.Player;

import living.relics.habilities.WardenEcho;

public class WardenSword extends WeaponItem {
        private final WardenEcho echo = new WardenEcho();

        public WardenSword() {

                super(
                                WeaponSettings.create()
                                                .id("warden_sword")
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
                                                .relic(() -> ModItems.WARDEN_CORE)
                                                // Apariencia
                                                .titleColor(TextColor.fromRgb(0x408572))
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
        protected void onHit(Player player,LivingEntity target,ItemStack stack) {
                if (player.getRandom().nextFloat() <= 0.15F) {
                        echo.execute(player,target);
                }
        }
}