package com.stellarwind23.terrarian_reforging.mixin;

import com.stellarwind23.terrarian_reforging.util.TRQualityRoller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantMenu.class)
public abstract class MerchantMenuMixin {

    @Inject(method = "quickMoveStack", at = @At("HEAD"))
    private void tr$rollBeforeShiftMove(Player player, int i, CallbackInfoReturnable<ItemStack> cir) {
        if (i != 2 || player.level().isClientSide()) return;   // 2 = trade result slot

        ItemStack result = ((AbstractContainerMenu) (Object) this).getSlot(2).getItem();
        TRQualityRoller.tryRollNew(player.level().registryAccess(), result, player.getRandom());
    }
}