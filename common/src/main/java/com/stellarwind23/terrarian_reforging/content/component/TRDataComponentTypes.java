package com.stellarwind23.terrarian_reforging.content.component;

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

    public static RegistrySupplier<DataComponentType<TRQualityComponent>> TR_QUALITY_COMPONENT;

    public static void init() {

        TR_COMPONENTS = DeferredRegister.create(TerrarianReforging.MOD_ID, Registries.DATA_COMPONENT_TYPE);

        TR_QUALITY_COMPONENT = registerComponent(TerrarianReforging.CMP_NAME, builder ->
                builder.persistent(TRQualityComponent.CODEC)
                .networkSynchronized(TRQualityComponent.STREAM_CODEC)
                .cacheEncoding()
                );

        TR_COMPONENTS.register();
    }

    private static <T> RegistrySupplier<DataComponentType<T>> registerComponent(String name, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        return TR_COMPONENTS.register(name, () -> {
            ResourceKey.create(Registries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(TerrarianReforging.MOD_ID, name));
            return unaryOperator.apply(DataComponentType.builder()).build();
        });
    }
}
