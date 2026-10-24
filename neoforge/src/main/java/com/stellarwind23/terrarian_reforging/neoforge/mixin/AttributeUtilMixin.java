package com.stellarwind23.terrarian_reforging.neoforge.mixin;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.stellarwind23.terrarian_reforging.content.component.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.component.TRQualityComponent;
import com.stellarwind23.terrarian_reforging.util.TRTooltipColors;
import com.stellarwind23.terrarian_reforging.util.TRTooltipFolding;
import com.stellarwind23.terrarian_reforging.util.client.TRHideReforgeState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import net.neoforged.neoforge.common.util.AttributeUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;
import java.util.function.Consumer;

@Mixin(AttributeUtil.class)
public class AttributeUtilMixin {

    @WrapOperation(
            method = "applyModifierTooltips",
            at = @At(value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/common/util/AttributeUtil;applyTextFor(Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;Lcom/google/common/collect/Multimap;Lnet/neoforged/neoforge/common/util/AttributeTooltipContext;)V"))
    private static void tr$fold(ItemStack stack, Consumer<Component> tooltip, Multimap<Holder<Attribute>, AttributeModifier> modifierMap, AttributeTooltipContext ctx, Operation<Void> original, @Local EquipmentSlotGroup group) {

        TRQualityComponent qc = TRHideReforgeState.hideQuality() ? null
                : stack.get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());

        if (qc != null && qc.quality().isBound()) {
            for (TRTooltipFolding.BaseRow row : TRTooltipFolding.foldBases(qc, group, modifierMap)) {
                Attribute attr = row.attr().value();
                if (row.weapon()) {
                    double entityBase = ctx.player() == null ? 0 : Objects.requireNonNull(ctx.player()).getAttributeBaseValue(row.attr());
                    double amt = row.value() - TRTooltipFolding.playerBase(row.attr()) + entityBase;
                    tooltip.accept(Component.literal(" ")
                            .append(attr.toBaseComponent(amt, entityBase, false, ctx.flag()))
                            .withStyle(row.modified() ? TRTooltipColors.MODIFIED_WEAPON
                                    : ChatFormatting.DARK_GREEN));
                } else if (row.value() != 0) {
                    var shown = new AttributeModifier(row.mod().id(), row.value(),
                            AttributeModifier.Operation.ADD_VALUE);
                    MutableComponent line = attr.toComponent(shown, ctx.flag());
                    tooltip.accept(row.modified() ? line.withStyle(TRTooltipColors.MODIFIED_ARMOR) : line);
                }
            }
        }
        original.call(stack, tooltip, modifierMap, ctx);   // everything not folded renders as NeoForge normally
    }
}