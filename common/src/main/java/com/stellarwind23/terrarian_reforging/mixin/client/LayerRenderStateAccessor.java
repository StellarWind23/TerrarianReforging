package com.stellarwind23.terrarian_reforging.mixin.client;

import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackRenderState.LayerRenderState.class)
public interface LayerRenderStateAccessor {

    @Accessor("transform")
    default ItemTransform tr$getTransform() { throw new NotImplementedException(); }
}
