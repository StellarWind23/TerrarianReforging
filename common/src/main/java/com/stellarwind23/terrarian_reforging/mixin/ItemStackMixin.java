package com.stellarwind23.terrarian_reforging.mixin;

import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.content.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.TRQualityComponent;
import com.stellarwind23.terrarian_reforging.util.TRQualityRoller;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
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

    @Inject(method = "onCraftedBy", at = @At("HEAD"))
    private void tr$rollOnFirstCraft(Player player, int i, CallbackInfo ci) {
        if(player.level().isClientSide()) return; //Server only

        ItemStack self = (ItemStack) (Object) this;
        if(!TRQualityRoller.accepts(self)) return;
        if(self.has(TRDataComponentTypes.TR_QUALITY_COMPONENT.get())) return; //Already has quality
        if(player.getRandom().nextFloat() >= TerrarianReforging.FIRST_CRAFT_CHANCE) return;

        TRQualityRoller.roll(player.level().registryAccess(), self, player.getRandom())
                .ifPresent(q -> TRQualityRoller.apply(self, q));
    }
}
