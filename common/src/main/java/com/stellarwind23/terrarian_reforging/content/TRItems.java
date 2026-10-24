package com.stellarwind23.terrarian_reforging.content;

import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public class TRItems {

    private static DeferredRegister<Item> TR_ITEMS;

    public static RegistrySupplier<Item> REFORGING_TEMPLATE;

    public static void init() {

        TR_ITEMS = DeferredRegister.create(TerrarianReforging.MOD_ID, Registries.ITEM);

        REFORGING_TEMPLATE = register("reforging_template", properties -> new Item(properties.rarity(Rarity.UNCOMMON)));

        TR_ITEMS.register();

        CreativeTabRegistry.append(CreativeModeTabs.INGREDIENTS, REFORGING_TEMPLATE.get());
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
