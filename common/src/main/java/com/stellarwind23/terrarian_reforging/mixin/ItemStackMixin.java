package com.stellarwind23.terrarian_reforging.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.content.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.TRQualityComponent;
import com.stellarwind23.terrarian_reforging.util.TRQualityRoller;
import com.stellarwind23.terrarian_reforging.util.TRTooltipFolding;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.apache.commons.lang3.function.TriConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "getHoverName", at = @At("TAIL"), cancellable = true)
    private void tr$addPrefix(CallbackInfoReturnable<Component> cir) {
        ItemStack self = (ItemStack) (Object) this;
        TRQualityComponent qc = self.get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());

        if(qc != null) {
            cir.setReturnValue(qc.quality().value().decorate(qc.quality().unwrapKey().orElseThrow().identifier(), cir.getReturnValue()));
        }
    }

    @ModifyReturnValue(method = "getRarity", at = @At("RETURN"))
    private Rarity tr$addRarity(Rarity original) {
        ItemStack self = (ItemStack) (Object) this;
        TRQualityComponent qc = self.get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());
        if (qc == null || !qc.quality().isBound()) return original;

        return qc.quality().value().rarity()
                .filter(q -> q.compareTo(original) > 0)
                .orElse(original);
    }

    @ModifyVariable(
            method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V",
            at = @At("HEAD"), argsOnly = true, name = "triConsumer")
    private TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> tr$foldQuality(
            TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> triConsumer,
            EquipmentSlotGroup equipmentSlotGroup) {

        TRQualityComponent qc = ((ItemStack) (Object) this).get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());
        if (qc == null || !qc.quality().isBound()) return triConsumer;
        return TRTooltipFolding.wrap(qc, equipmentSlotGroup, triConsumer);
    }
}
