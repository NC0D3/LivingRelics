package living.relics.habilities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface RelicAbility {

    void execute(Player player, LivingEntity target);

}