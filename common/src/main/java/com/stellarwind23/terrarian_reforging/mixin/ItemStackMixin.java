package com.stellarwind23.terrarian_reforging.mixin;

import com.stellarwind23.terrarian_reforging.content.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.TRQualityComponent;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(
            method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V",
            at = @At("TAIL")
    )
    private void tr$addQualityModifiers(EquipmentSlot equipmentSlot, BiConsumer<Holder<Attribute>, AttributeModifier> biConsumer, CallbackInfo ci)
    {
        TRQualityComponent qc = ((ItemStack) (Object) this).get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());

        if(qc != null) {
            qc.forEachModifier(equipmentSlot, biConsumer);
        }
    }
}
