package com.stellarwind23.terrarian_reforging.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.stellarwind23.terrarian_reforging.content.component.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.component.TRQualityComponent;
import com.stellarwind23.terrarian_reforging.util.client.TRHideReforgeState;
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
        boolean hide = false;

        if ((Object) this instanceof SmithingScreen screen
                && hoveredSlot != null && hoveredSlot.index == 3) {            // result slot

            TRQualityComponent out = itemStack.get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());
            ItemStack base = screen.getMenu().getSlot(1).getItem();            // base slot
            TRQualityComponent in = base.get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());

            hide = out != null && !out.equals(in);
        }

        boolean prev = TRHideReforgeState.setHideQuality(hide);
        try {
            return original.call(itemStack);
        } finally {
            TRHideReforgeState.setHideQuality(prev);
        }
    }
}