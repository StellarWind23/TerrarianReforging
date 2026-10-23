package com.stellarwind23.terrarian_reforging.mixin.client;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackRenderState.class)
public interface ItemStackRenderStateAccessor {

    @Accessor("activeLayerCount")
    int tr$activeLayerCount();

    @Accessor("layers")
    ItemStackRenderState.LayerRenderState[] tr$layers();
}
