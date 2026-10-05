package living.relics;

import living.relics.item.ModItems;
import living.relics.EntityExtensions.BossBarEvents;
import living.relics.EntityExtensions.Warden;
import living.relics.EntityExtensions.ElderGuardian;
import living.relics.EntityExtensions.Wither;
import living.relics.block.ModBlocks;
import living.relics.entity.RelicEntityRegistry;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

public class LivingRelics implements ModInitializer {

    public static final String MOD_ID = "living_relics";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Living Relics");
        PolymerResourcePackUtils.addModAssets(LivingRelics.MOD_ID);
        ModItems.initialize();
        ModBlocks.initialize();
        new Warden();
        new ElderGuardian();
        new Wither();
        BossBarEvents.register();
        RelicEntityRegistry.initialize();

    }

}