package com.stellarwind23.terrarian_reforging.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stellarwind23.terrarian_reforging.content.component.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.component.TRQualityComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @WrapMethod(method = "render")
    private void tr$scale(ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, Operation<Void> original) {
        TRQualityComponent qc = itemStack.get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());
        float s = (qc != null && qc.quality().isBound()) ? qc.quality().value().scaleFor(itemDisplayContext) : 1F;
        if (s == 1F) {
            original.call(itemStack, itemDisplayContext, bl, poseStack, multiBufferSource, i, j, bakedModel);
            return;
        }
        poseStack.pushPose();
        poseStack.scale(s, s, s);
        original.call(itemStack, itemDisplayContext, bl, poseStack, multiBufferSource, i, j, bakedModel);
        poseStack.popPose();
    }
}