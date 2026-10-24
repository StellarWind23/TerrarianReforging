package com.stellarwind23.terrarian_reforging.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.stellarwind23.terrarian_reforging.content.component.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.component.TRQualityComponent;
import com.stellarwind23.terrarian_reforging.util.TRQualityRoller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SmithingTransformRecipe.class)
public abstract class SmithingTransformRecipeMixin {

    @ModifyReturnValue(method = "assemble*", at = @At("RETURN"))
    private ItemStack tr$refreshQuality(ItemStack result) {
        if (result.isEmpty()) return result;
        TRQualityComponent qc = result.get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());
        if (qc != null && qc.quality().isBound()) {
            TRQualityRoller.apply(result, qc.quality());   // recomputes max damage + enchantability from the new item
        }
        return result;
    }
}