package com.stellarwind23.terrarian_reforging.mixin;

import com.stellarwind23.terrarian_reforging.util.TRQualityRoller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin {

    @Inject(method = "quickMoveStack", at = @At("HEAD"))
    private void tr$rollBeforeShiftMove(Player player, int i, CallbackInfoReturnable<ItemStack> cir) {
        if (i != 0 || player.level().isClientSide()) return;   // 0 = result slot

        ItemStack result = ((AbstractContainerMenu) (Object) this).getSlot(0).getItem();
        TRQualityRoller.tryRollNew(player.level().registryAccess(), result, player.getRandom());
    }
}