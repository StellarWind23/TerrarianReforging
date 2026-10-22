package com.stellarwind23.terrarian_reforging.content;

import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.UnaryOperator;

public class TRDataComponentTypes {

    private static DeferredRegister<DataComponentType<?>> TR_COMPONENTS;

    private static <T>RegistrySupplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return TR_COMPONENTS.register(name, () -> {
            ResourceKey.create(Registries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(
                    TerrarianReforging.MOD_ID,
                    name
            ));
            return operator.apply(DataComponentType.builder()).build();
        });
    }
}
