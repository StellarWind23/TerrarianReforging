package com.stellarwind23.terrarian_reforging.content;

import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;
import java.util.function.Function;

public class TRItems {

    private static DeferredRegister<Item> TR_ITEMS;

    public static RegistrySupplier<Item> REFORGING_TOKEN;
    public static RegistrySupplier<Item> WHETSTONE;
    public static RegistrySupplier<Item> COPPER_WOOL;

    @SuppressWarnings("unchecked")
    public static void init() {

        TR_ITEMS = DeferredRegister.create(TerrarianReforging.MOD_ID, Registries.ITEM);

        REFORGING_TOKEN = register("reforging_token", properties -> new SmithingTemplateItem(
                Component.translatable("tag.item.terrarian_reforging.accepted_equipment")      // "Applies to:" value
                        .withStyle(ChatFormatting.BLUE),
                Component.translatable("tag.item.terrarian_reforging.reforge_materials")       // "Ingredients:" value
                        .withStyle(ChatFormatting.BLUE),
                Component.translatable("item.terrarian_reforging.reforging_token.base_slot"),       // hover text on the empty base slot
                Component.translatable("item.terrarian_reforging.reforging_token.additions_slot"),  // hover text on the empty addition slot
                List.of(/* base slot icon sprite ids */),
                List.of(/* addition slot icon sprite ids */),
                properties.rarity(Rarity.UNCOMMON)));

        WHETSTONE = register("whetstone", p -> new TooltipItem(p.rarity(Rarity.UNCOMMON).durability(17),
                tip("whetstone.1"), tip("whetstone.2")));
        COPPER_WOOL = register("copper_wool", p -> new TooltipItem(p.rarity(Rarity.UNCOMMON).durability(15),
                tip("copper_wool.1"), tip("copper_wool.2")));

        TR_ITEMS.register();

        CreativeTabRegistry.append(CreativeModeTabs.INGREDIENTS, REFORGING_TOKEN);
        CreativeTabRegistry.append(CreativeModeTabs.INGREDIENTS, WHETSTONE);
        CreativeTabRegistry.append(CreativeModeTabs.INGREDIENTS, COPPER_WOOL);
    }

    private static Component tip(String key) {
        return Component.translatable("item.terrarian_reforging." + key).withStyle(ChatFormatting.GRAY);
    }

    private static <T extends Item>RegistrySupplier<T> register(String name, Function<Item.Properties, T> constructor) {
        return TR_ITEMS.register(name, () -> {
            ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TerrarianReforging.MOD_ID, name));
            Item.Properties properties = new Item.Properties();
            properties = properties.setId(key);
            return constructor.apply(properties);
        });
    }
}
