package living.relics.block;

import living.relics.LivingRelics;

import living.relics.block.material.VivianiteOre;

public final class ModBlocks {

    private ModBlocks() {
    }

    public static final VivianiteOre VIVIANITE_ORE = new VivianiteOre();

    public static void initialize() {

        LivingRelics.LOGGER.info(
                "Registering Living Relics Blocks"
        );

    }

}