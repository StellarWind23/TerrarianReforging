package com.stellarwind23.terrarian_reforging.content.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.stellarwind23.terrarian_reforging.util.TRQualityRoller;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

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
    public boolean matches(SmithingRecipeInput input, Level level) {
        if (!template.test(input.template()) || !base.test(input.base()) || !addition.test(input.addition())) {
            return false;
        }

        // 1.21.1: repair materials are asked of the item itself (no REPAIRABLE component yet)
        ItemStack baseStack = input.base();
        if (!baseStack.getItem().isValidRepairItem(baseStack, input.addition())) return false;

        return TRQualityRoller.hasCandidates(level.registryAccess(), baseStack);
    }

    // 1.21.1's SmithingRecipe asks about each slot by stack instead of exposing the ingredients
    @Override
    public boolean isTemplateIngredient(ItemStack stack) { return template.test(stack); }

    @Override
    public boolean isBaseIngredient(ItemStack stack) { return base.test(stack); }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) { return addition.test(stack); }

    @Override
    public @NotNull ItemStack assemble(SmithingRecipeInput in, HolderLookup.Provider registries) {
        ItemStack out = in.base().copyWithCount(1);
        return TRQualityRoller.roll(registries, out, RandomSource.create())
                .map(q -> { TRQualityRoller.apply(out, q); return out; })
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;   // the result depends on the roll, so there's no fixed output to show
    }

    @Override
    public @NotNull RecipeSerializer<TRReforgeRecipe> getSerializer() { return TRRecipeSerializers.REFORGE.get(); }
}