package living.relics.block;

import living.relics.LivingRelics;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;

import eu.pb4.polymer.core.api.block.SimplePolymerBlock;
import eu.pb4.polymer.core.api.item.VanillaModeledPolymerItem;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class BaseBlock extends SimplePolymerBlock implements PolymerTexturedBlock {

    protected final String id;

    protected final Supplier<Item> dropItem;

    protected final BaseBlockItem blockItem;

    private BlockState polymerState;

    public BaseBlock(
            String id,
            Properties properties,
            Block vanillaBlock,
            BlockModelType modelType,
            Supplier<Item> dropItem) {

        super(

                properties.setId(

                        ResourceKey.create(

                                BuiltInRegistries.BLOCK.key(),

                                Identifier.fromNamespaceAndPath(
                                        LivingRelics.MOD_ID,
                                        id)

                        )

                ),

                vanillaBlock

        );

        this.id = id;
        this.dropItem = dropItem;

        /*
         * IMPORTANTE:
         * Crear Polymer state ANTES del registro
         */

        PolymerBlockModel model = PolymerBlockModel.of(

                Identifier.fromNamespaceAndPath(
                        LivingRelics.MOD_ID,
                        "block/" + id)

        );

        this.polymerState = PolymerBlockResourceUtils.requestBlock(
                modelType,
                model);

        if (this.polymerState == null) {

            throw new IllegalStateException(
                    "No Polymer block state available for: " + id);

        }

        /*
         * Ahora sí registramos el bloque
         */

        Registry.register(

                BuiltInRegistries.BLOCK,

                Identifier.fromNamespaceAndPath(
                        LivingRelics.MOD_ID,
                        id),

                this

        );

        /*
         * Registramos automáticamente el item
         */

        this.blockItem = new BaseBlockItem(this);

    }

    public Item getDropItem() {

        return dropItem.get();

    }

    public Item getBlockItem() {

        return blockItem;

    }

    @Override
    public BlockState getPolymerBlockState(
            BlockState state,
            PacketContext context) {

        return polymerState;

    }

    @Override
    public boolean handleMiningOnServer(
            ItemStack tool,
            BlockState state,
            net.minecraft.core.BlockPos pos,
            net.minecraft.server.level.ServerPlayer player) {

        return true;

    }

    private class BaseBlockItem extends BlockItem implements VanillaModeledPolymerItem {

        public BaseBlockItem(Block block) {

            super(

                    block,

                    new Item.Properties()

                            .setId(

                                    ResourceKey.create(

                                            BuiltInRegistries.ITEM.key(),

                                            Identifier.fromNamespaceAndPath(
                                                    LivingRelics.MOD_ID,
                                                    id)

                                    )

                            )

                            .component(

                                    DataComponents.CUSTOM_NAME,

                                    Component.translatable(
                                            "block."
                                                    + LivingRelics.MOD_ID
                                                    + "."
                                                    + id)

                                            .setStyle(
                                                    Style.EMPTY.withItalic(false))

                            )

            );

            Registry.register(

                    BuiltInRegistries.ITEM,

                    Identifier.fromNamespaceAndPath(
                            LivingRelics.MOD_ID,
                            id),

                    this

            );

        }

        @Override
        public Item getPolymerItem(
                ItemStack stack,
                PacketContext context) {

            return Items.PAPER;

        }

        @Override
        public Identifier getPolymerItemModel(
                ItemStack stack,
                PacketContext context,
                HolderLookup.Provider lookup) {

            return Identifier.fromNamespaceAndPath(

                    LivingRelics.MOD_ID,

                    id

            );

        }

        @Override
        public Component getName(ItemStack stack) {

            return Component.translatable(

                    "block."
                            + LivingRelics.MOD_ID
                            + "."
                            + id

            );

        }

    }

}