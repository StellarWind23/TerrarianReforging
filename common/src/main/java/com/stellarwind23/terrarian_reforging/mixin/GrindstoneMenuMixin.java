package com.stellarwind23.terrarian_reforging.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.stellarwind23.terrarian_reforging.util.TRGrindstone;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GrindstoneMenu.class)
public class GrindstoneMenuMixin {

    @ModifyReturnValue(method = "computeResult", at = @At("RETURN"))
    private ItemStack tr$special(ItemStack original, ItemStack input, ItemStack additional) {
        var k = TRGrindstone.kind(input, additional);
        return k == TRGrindstone.Kind.NONE ? original : TRGrindstone.result(k, input, additional);
    }
}