package com.stellarwind23.terrarian_reforging.content.component;

import com.mojang.serialization.Codec;
import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.util.TRTooltipColors;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record TRSharpenedComponent(int uses) implements TooltipProvider {
    public static final int MAX_USES = 50;      // config later
    public static final double BONUS = 1.0;     // attack damage; 1.0 = half a heart
    public static final Identifier MODIFIER_ID =
            Identifier.fromNamespaceAndPath(TerrarianReforging.MOD_ID, "sharpened");

    public static final Codec<TRSharpenedComponent> CODEC =
            ExtraCodecs.POSITIVE_INT.xmap(TRSharpenedComponent::new, TRSharpenedComponent::uses);
    public static final StreamCodec<ByteBuf, TRSharpenedComponent> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(TRSharpenedComponent::new, TRSharpenedComponent::uses);

    @Override
    public void addToTooltip(Item.@NonNull TooltipContext ctx, Consumer<Component> out, @NonNull TooltipFlag flag, @NonNull DataComponentGetter components) {
        out.accept(Component.translatable("tr_sharpened.tooltip", uses, MAX_USES)
                .withStyle(TRTooltipColors.POSITIVE));
    }

    public void forEachModifier(EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> out) {
        if (slot != EquipmentSlot.MAINHAND) return;
        out.accept(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(MODIFIER_ID, BONUS, AttributeModifier.Operation.ADD_VALUE));
    }
}