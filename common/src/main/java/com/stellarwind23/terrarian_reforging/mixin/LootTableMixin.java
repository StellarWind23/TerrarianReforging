package com.stellarwind23.terrarian_reforging.mixin;

import com.stellarwind23.terrarian_reforging.util.TRQualityRoller;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Consumer;

@Mixin(LootTable.class)
public abstract class LootTableMixin {

    @Shadow
    public abstract ContextKeySet getParamSet();   // type name differs by version, see note below

    @ModifyVariable(
            method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"), argsOnly = true)
    private Consumer<ItemStack> tr$rollLootQuality(Consumer<ItemStack> consumer, LootContext lootContext) {
        if (getParamSet() != LootContextParamSets.CHEST) return consumer;   // chests only, delete this line for every table

        ServerLevel level = lootContext.getLevel();
        RandomSource rnd = lootContext.getRandom();
        return stack -> {
            TRQualityRoller.tryRollNew(level.registryAccess(), stack, rnd);
            consumer.accept(stack);
        };
    }
}