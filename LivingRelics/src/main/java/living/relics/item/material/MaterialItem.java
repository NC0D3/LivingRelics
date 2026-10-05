package living.relics.item.material;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.core.api.item.VanillaModeledPolymerItem;

import living.relics.LivingRelics;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MaterialItem extends SimplePolymerItem implements VanillaModeledPolymerItem {

    protected final String id;

    public MaterialItem(
            String id,
            Properties properties
    ) {

        super(

                properties
                        .setId(
                                ResourceKey.create(
                                        BuiltInRegistries.ITEM.key(),
                                        Identifier.fromNamespaceAndPath(
                                                LivingRelics.MOD_ID,
                                                id
                                        )
                                )
                        )
                        .component(
                                DataComponents.CUSTOM_NAME,
                                Component.translatable(
                                        "item." + LivingRelics.MOD_ID + "." + id
                                ).setStyle(
                                        Style.EMPTY.withItalic(false)
                                )
                        ),

                Items.PAPER

        );

        this.id = id;

    }

    @Override
    public Identifier getPolymerItemModel(
            ItemStack stack,
            PacketContext context,
            HolderLookup.Provider lookup
    ) {

        return Identifier.fromNamespaceAndPath(
                LivingRelics.MOD_ID,
                id
        );

    }

}