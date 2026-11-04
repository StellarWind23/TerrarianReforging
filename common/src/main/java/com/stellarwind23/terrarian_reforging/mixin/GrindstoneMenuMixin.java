package com.stellarwind23.terrarian_reforging.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.stellarwind23.terrarian_reforging.content.TRItems;
import com.stellarwind23.terrarian_reforging.content.component.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.util.TRQualityRoller;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GrindstoneMenu.class)
public class GrindstoneMenuMixin {

    @ModifyReturnValue(method = "computeResult", at = @At("RETURN"))
    private ItemStack tr$cleanse(ItemStack original, ItemStack itemStack, ItemStack itemStack2) {
        Item wool = TRItems.COPPER_WOOL.get();
        boolean aWool = itemStack.is(wool), bWool = itemStack2.is(wool);
        if (aWool == bWool) return original;               // need exactly one wool

        ItemStack target = aWool ? itemStack2 : itemStack;
        if (target.isEmpty() || target.getCount() != 1
                || !target.has(TRDataComponentTypes.TR_QUALITY_COMPONENT.get())) return original;

        ItemStack result = target.copy();
        TRQualityRoller.clear(result);
        return result;
    }
}