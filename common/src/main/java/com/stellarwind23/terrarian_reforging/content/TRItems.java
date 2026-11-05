package com.stellarwind23.terrarian_reforging.content;

import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class TRItems {

    private static DeferredRegister<Item> TR_ITEMS;

    public static RegistrySupplier<Item> REFORGING_TOKEN;
    public static RegistrySupplier<Item> WHETSTONE;
    public static RegistrySupplier<Item> COPPER_WOOL;

    @SuppressWarnings("unchecked")
    public static void init() {

        TR_ITEMS = DeferredRegister.create(TerrarianReforging.MOD_ID, Registries.ITEM);

        // 1.21.1: no Properties argument, but an extra "upgrade description" component
        REFORGING_TOKEN = registerItem("reforging_token", () -> new SmithingTemplateItem(
                Component.translatable("tag.item.terrarian_reforging.accepted_equipment")      // "Applies to:" value
                        .withStyle(ChatFormatting.BLUE),
                Component.translatable("tag.item.terrarian_reforging.reforge_materials")       // "Ingredients:" value
                        .withStyle(ChatFormatting.BLUE),
                Component.translatable("upgrade.terrarian_reforging.reforging")                // title line under the item name
                        .withStyle(ChatFormatting.GRAY),
                Component.translatable("item.terrarian_reforging.reforging_token.base_slot"),       // hover text on the empty base slot
                Component.translatable("item.terrarian_reforging.reforging_token.additions_slot"),  // hover text on the empty addition slot
                List.of(/* base slot icon sprite ids */),
                List.of(/* addition slot icon sprite ids */)));

        WHETSTONE = register("whetstone", p -> new TooltipItem(p.durability(20),
                tip("whetstone.1"), tip("whetstone.2")));
        COPPER_WOOL = register("copper_wool", p -> new TooltipItem(p.durability(25),
                tip("copper_wool.1"), tip("copper_wool.2")));

        TR_ITEMS.register();

        CreativeTabRegistry.append(CreativeModeTabs.INGREDIENTS, REFORGING_TOKEN, WHETSTONE, COPPER_WOOL);
    }

    private static Component tip(String key) {
        return Component.translatable("item.terrarian_reforging." + key).withStyle(ChatFormatting.DARK_GRAY);
    }

    private static <T extends Item> RegistrySupplier<T> register(String name, Function<Item.Properties, T> constructor) {
        return TR_ITEMS.register(name, () -> constructor.apply(new Item.Properties()));
    }

    private static <T extends Item> RegistrySupplier<T> registerItem(String name, Supplier<T> factory) {
        return TR_ITEMS.register(name, factory);
    }
}