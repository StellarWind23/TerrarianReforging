package com.stellarwind23.terrarian_reforging.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.content.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.TRQuality;
import com.stellarwind23.terrarian_reforging.content.TRQualityComponent;
import com.stellarwind23.terrarian_reforging.util.TRQualityRoller;
import com.stellarwind23.terrarian_reforging.util.TRTooltipFolding;
import com.stellarwind23.terrarian_reforging.util.client.TRClientState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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

    @ModifyReturnValue(method = "getHoverName", at = @At("RETURN"))
    private Component tr$qualityName(Component original) {
        ItemStack self = (ItemStack) (Object) this;
        TRQualityComponent qc = self.get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());
        if (qc == null || !qc.quality().isBound()) return original;

        // Smithing result preview: scrambled prefix, readable base name, forced white
        if (TRClientState.hideQuality()) {
            return Component.translatable("tr_quality.tooltip.hidden", original).withStyle(ChatFormatting.GREEN);
        }

        // Normal display
        Identifier id = qc.quality().unwrapKey().map(ResourceKey::identifier).orElse(null);
        if (id == null) return original;
        TRQuality q = qc.quality().value();
        return q.decorate(id, original).copy().withStyle(q.styleOr(Style.EMPTY));
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
            at = @At("HEAD"), argsOnly = true, name = "triConsumer"
    )
    private TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> tr$foldQuality(TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> triConsumer, EquipmentSlotGroup equipmentSlotGroup) {
        //Hide size modifier
        if(TRClientState.hideQuality()) return triConsumer;

        TRQualityComponent qc = ((ItemStack) (Object) this).get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());
        if (qc == null || !qc.quality().isBound()) return triConsumer;
        return TRTooltipFolding.wrap(qc, equipmentSlotGroup, triConsumer);
    }
}
