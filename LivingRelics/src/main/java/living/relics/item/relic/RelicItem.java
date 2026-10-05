package living.relics.item.relic;

import living.relics.LivingRelics;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.core.api.item.VanillaModeledPolymerItem;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import net.minecraft.world.item.component.ItemLore;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;

public class RelicItem extends SimplePolymerItem implements VanillaModeledPolymerItem {

    private final String id;

    @Override
    public Identifier getPolymerItemModel(
            ItemStack stack,
            PacketContext context,
            HolderLookup.Provider lookup) {

        return Identifier.fromNamespaceAndPath(
                LivingRelics.MOD_ID,
                id);

    }

    public RelicItem(
            String id,
            int color,
            Properties properties) {

        super(
                properties
                        .setId(
                                ResourceKey.create(
                                        BuiltInRegistries.ITEM.key(),
                                        Identifier.fromNamespaceAndPath(
                                                LivingRelics.MOD_ID,
                                                id)))
                        .component(
                                DataComponents.LORE,
                                new ItemLore(
                                        List.of(
                                                Component.literal("RELIC")
                                                        .setStyle(
                                                                Style.EMPTY
                                                                        .withColor(0xff00ff)
                                                                        .withBold(true)
                                                                        .withItalic(true)))))
                        .component(
                                DataComponents.CUSTOM_NAME,
                                Component.translatable(
                                        "item."
                                                + LivingRelics.MOD_ID
                                                + "."
                                                + id)
                                        .setStyle(
                                                Style.EMPTY
                                                        .withColor(color)
                                                        .withItalic(false))),

                Items.PAPER);

        this.id = id;
    }
}