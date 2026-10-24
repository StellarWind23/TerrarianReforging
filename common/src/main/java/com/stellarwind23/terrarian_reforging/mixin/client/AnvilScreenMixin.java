package com.stellarwind23.terrarian_reforging.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.stellarwind23.terrarian_reforging.util.client.TRNameState;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin {
    @WrapOperation(
            method = {"slotChanged", "onNameChanged"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getHoverName()Lnet/minecraft/network/chat/Component;"))
    private Component tr$plainAnvilName(ItemStack instance, Operation<Component> original) {
        return TRNameState.withoutQuality(() -> original.call(instance));
    }
}