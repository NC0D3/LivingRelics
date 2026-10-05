package living.relics.block.material;

import living.relics.block.BaseBlock;
import living.relics.item.ModItems;

import eu.pb4.polymer.blocks.api.BlockModelType;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class VivianiteOre extends BaseBlock {

    public VivianiteOre() {

        super(
                "vivianite_ore",
                Block.Properties.of()
                        .requiresCorrectToolForDrops()
                        .strength(5.0F, 6.0F),

                Blocks.DEEPSLATE,

                BlockModelType.FULL_BLOCK,

                () -> ModItems.VIVIANITE_CRYSTAL

        );

    }

}