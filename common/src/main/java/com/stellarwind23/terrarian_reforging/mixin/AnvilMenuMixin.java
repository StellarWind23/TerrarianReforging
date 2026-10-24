package com.stellarwind23.terrarian_reforging.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.stellarwind23.terrarian_reforging.util.client.TRNameState;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    @WrapMethod(method = "createResult")
    private void tr$plainAnvilName(Operation<Void> original) {
        TRNameState.withoutQuality(() -> { original.call(); return null; });
    }
}