package com.stellarwind23.terrarian_reforging.mixin.client;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackRenderState.class)
public interface ItemStackRenderStateAccessor {

    @Accessor("activeLayerCount")
    default int tr$activeLayerCount() { throw new NotImplementedException();}

    @Accessor("layers")
    default ItemStackRenderState.LayerRenderState[] tr$layers() { throw new NotImplementedException(); }
}
