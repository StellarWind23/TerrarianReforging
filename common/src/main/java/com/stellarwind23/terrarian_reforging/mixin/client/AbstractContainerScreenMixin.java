package com.stellarwind23.terrarian_reforging.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.stellarwind23.terrarian_reforging.content.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.util.client.TRClientState;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Shadow
    protected Slot hoveredSlot;

    @SuppressWarnings("ConstantValue")
    @WrapMethod(method = "getTooltipFromContainerItem")
    private List<Component> tr$hideReforgePreview(ItemStack itemStack, Operation<List<Component>> original) {
        boolean hide = (Object) this instanceof SmithingScreen
                && hoveredSlot != null && hoveredSlot.index == 3   // smithing result slot
                && itemStack.has(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());

        boolean prev = TRClientState.setHideQuality(hide);
        try {
            return original.call(itemStack);
        } finally {
            TRClientState.setHideQuality(prev);
        }
    }
}