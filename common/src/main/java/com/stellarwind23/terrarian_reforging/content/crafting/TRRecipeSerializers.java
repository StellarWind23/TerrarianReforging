package com.stellarwind23.terrarian_reforging.content.crafting;

import com.mojang.serialization.MapCodec;
import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jspecify.annotations.NonNull;

public class TRRecipeSerializers {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(TerrarianReforging.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final RegistrySupplier<RecipeSerializer<TRReforgeRecipe>> REFORGE =
            SERIALIZERS.register("reforge",
                    () -> new RecipeSerializer<>() {
                        @Override
                        public @NonNull MapCodec<TRReforgeRecipe> codec() {
                            return TRReforgeRecipe.CODEC;
                        }

                        @Override
                        public @NonNull StreamCodec<RegistryFriendlyByteBuf, TRReforgeRecipe> streamCodec() {
                            return TRReforgeRecipe.STREAM_CODEC;
                        }
                    });

    public static void init() { SERIALIZERS.register(); }
}