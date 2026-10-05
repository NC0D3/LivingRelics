package living.relics.item.weapon;

import living.relics.item.ModItems;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.List;

import living.relics.habilities.GuardianBeam;

public class ElderGuardianSword extends WeaponItem {
    private final GuardianBeam gBeam = new GuardianBeam();
    private static final double RANGE = 10.0;

    public ElderGuardianSword() {

        super(
                WeaponSettings.create()
                        .id("elder_guardian_sword")
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
                        .relic(() -> ModItems.ELDER_GUARDIAN_EYE)
                        // Apariencia
                        .titleColor(TextColor.fromRgb(0xcfc2a1))
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
    public void onHit(Player player, LivingEntity target, ItemStack stack) {
        ServerLevel level = (ServerLevel) player.level();

        List<Monster> targets = level.getEntitiesOfClass(
                Monster.class,
                player.getBoundingBox().inflate(RANGE),
                entity -> entity.isAlive());

        if (targets.isEmpty()) {
            return;
        }

        Collections.shuffle(targets);

        float chance = player.getRandom().nextFloat();
        if (chance < 0.10F) {
            gBeam.castTriple(player, targets.stream().limit(3).toList());
        } else if (chance < 0.40F) {
            gBeam.castStrong(player, targets.getFirst());
        } else {
            gBeam.castLight(player, targets.getFirst());
        }
    }
}