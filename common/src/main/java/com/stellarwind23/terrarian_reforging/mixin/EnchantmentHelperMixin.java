package com.stellarwind23.terrarian_reforging.mixin;

import com.stellarwind23.terrarian_reforging.content.component.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.util.TRQualityRoller;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getEnchantmentCost", at = @At("TAIL"), cancellable = true)
    private static void tr$enchantability(RandomSource randomSource, int i, int j, ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
        if (itemStack.has(TRDataComponentTypes.TR_QUALITY_COMPONENT.get())) {
            cir.setReturnValue(TRQualityRoller.adjustEnchantability(cir.getReturnValue(), itemStack));
        }
    }
}
