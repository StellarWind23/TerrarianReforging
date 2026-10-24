package com.stellarwind23.terrarian_reforging.mixin.client;

import com.stellarwind23.terrarian_reforging.content.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.TRQualityComponent;
import com.stellarwind23.terrarian_reforging.util.client.TRHideState;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelResolver.class)
public abstract class ItemModelResolverMixin {

    @Inject(
            method = "updateForTopItem",
            at = @At("TAIL")
    )
    private void tr$applyQualityScale(ItemStackRenderState itemStackRenderState, ItemStack itemStack,
                                      ItemDisplayContext itemDisplayContext, Level level, ItemOwner itemOwner,
                                      int i, CallbackInfo ci) {

        TRQualityComponent qc = itemStack.get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());
        if (qc == null || !qc.quality().isBound()) return;

        //Hide size modifier
        if(TRHideState.hideQuality()) return;

        float s = qc.quality().value().scaleFor(itemDisplayContext);
        if (s == 1F) return;

        var acc = (ItemStackRenderStateAccessor) itemStackRenderState;
        var layers = acc.tr$layers();

        for (int n = 0; n < acc.tr$activeLayerCount(); n++) {
            var layer = layers[n];
            ItemTransform old = ((LayerRenderStateAccessor) layer).tr$getTransform();

            layer.setTransform(new ItemTransform(
                    new Vector3f(old.rotation()),
                    new Vector3f(old.translation()),
                    new Vector3f(old.scale()).mul(s)));   // base_scale * modifier
        }

        itemStackRenderState.appendModelIdentityElement(s);   // keeps the GUI cache from sharing images
    }
}
