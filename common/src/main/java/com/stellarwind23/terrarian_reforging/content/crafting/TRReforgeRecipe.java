package com.stellarwind23.terrarian_reforging.content.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.stellarwind23.terrarian_reforging.util.TRQualityRoller;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public record TRReforgeRecipe(Ingredient template, Ingredient base, Ingredient addition) implements SmithingRecipe {

    public static final MapCodec<TRReforgeRecipe> CODEC = RecordCodecBuilder.mapCodec(r -> r.group(
            Ingredient.CODEC.fieldOf("template").forGetter(TRReforgeRecipe::template),
            Ingredient.CODEC.fieldOf("base").forGetter(TRReforgeRecipe::base),
            Ingredient.CODEC.fieldOf("addition").forGetter(TRReforgeRecipe::addition)
    ).apply(r, TRReforgeRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TRReforgeRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, TRReforgeRecipe::template,
            Ingredient.CONTENTS_STREAM_CODEC, TRReforgeRecipe::base,
            Ingredient.CONTENTS_STREAM_CODEC, TRReforgeRecipe::addition,
            TRReforgeRecipe::new);

    @Override
    public boolean matches(@NonNull SmithingRecipeInput smithingRecipeInput, @NonNull Level level) {
        if (!template.test(smithingRecipeInput.template()) || !base.test(smithingRecipeInput.base()) || !addition.test(smithingRecipeInput.addition())) return false;

        Repairable repairable = smithingRecipeInput.base().get(DataComponents.REPAIRABLE);
        if (repairable == null || !repairable.isValidRepairItem(smithingRecipeInput.addition())) return false;

        return TRQualityRoller.hasCandidates(level.registryAccess(), smithingRecipeInput.base());
    }

    @Override
    public @NonNull Optional<Ingredient> templateIngredient() {
        return Optional.of(template);
    }

    @Override
    public @NonNull Ingredient baseIngredient() {
        return base;
    }

    @Override
    public @NonNull Optional<Ingredient> additionIngredient() {
        return Optional.of(addition);
    }

    @Override
    public @NonNull ItemStack assemble(SmithingRecipeInput in, HolderLookup.@NonNull Provider registries) {
        ItemStack out = in.base().copyWithCount(1);
        return TRQualityRoller.roll(registries, out, RandomSource.create())
                .map(q -> { TRQualityRoller.apply(out, q); return out; })
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public @NonNull RecipeSerializer<TRReforgeRecipe> getSerializer() { return TRRecipeSerializers.REFORGE.get(); }

    @Override
    public @NonNull PlacementInfo placementInfo() {
        return PlacementInfo.createFromOptionals(
                List.of(Optional.of(template), Optional.of(base), Optional.of(addition)));
    }
}
