package com.stellarwind23.terrarian_reforging.content;

import com.mojang.serialization.Codec;
import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.util.client.TRCHideState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.NonNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record TRQualityComponent(Holder<TRQuality> quality) implements TooltipProvider {

    public static final Codec<TRQualityComponent> CODEC =
            TRQuality.HOLDER_CODEC.xmap(TRQualityComponent::new, TRQualityComponent::quality);

    public static final StreamCodec<RegistryFriendlyByteBuf, TRQualityComponent> STREAM_CODEC =
            TRQuality.STREAM_CODEC.map(TRQualityComponent::new,TRQualityComponent::quality);

    @Override
    public void addToTooltip(Item.@NonNull TooltipContext ctx, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag flag, @NonNull DataComponentGetter getter) {

        //Hide Tooltip
        if(TRCHideState.hideQuality()) return;

        TRQuality q = quality.value();

        for (TRQuality.AttributeEntry ae : q.modifiers()) {
            consumer.accept(formatLine(ae));
        }

        q.durabilityScale().ifPresent(s -> scaleLine("durability", s, consumer));
        q.enchantability().ifPresent(b -> {
            if (b == 0) return;
            consumer.accept(Component.literal((b > 0 ? "+" : "") + b + " ")
                    .append(Component.translatable("tr_quality.tooltip.enchantability"))
                    .withStyle(b > 0 ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED));
        });
    }

    private static Component formatLine(TRQuality.AttributeEntry ae) {
        boolean pos = ae.amount() >= 0;
        boolean per = ae.operation() != AttributeModifier.Operation.ADD_VALUE;
        double shown = per ? ae.amount() * 100.0 : ae.amount();

        String num = (Math.abs(shown) % 1 == 0) ? String.valueOf((long) shown) : String.format("%.1f", shown);
        String value = (pos ? "+" : "") + num + (per ? "%" : "");   // negatives carry their own minus

        Component line;
        if (ae.attribute().is(TerrarianReforging.CUSTOM_DESCRIPTOR)) {
            // lang value gets the formatted number as %s, so you control wording and word order
            line = Component.translatable(customKey(ae.attribute()), value);
        } else {
            line = Component.literal(value + " ")
                    .append(Component.translatable(ae.attribute().value().getDescriptionId()));
        }
        return line.copy().withStyle(pos ? ChatFormatting.BLUE : ChatFormatting.RED);
    }

    private static String customKey(Holder<Attribute> attr) {
        Identifier id = attr.unwrapKey().map(ResourceKey::identifier).orElseThrow();
        return Util.makeDescriptionId(TerrarianReforging.CMP_NAME + ".attribute", id);
    }

    private static void scaleLine(String key, float scale, Consumer<Component> consumer) {
        int pct = Math.round((scale - 1F) * 100F);
        if (pct == 0) return; // a scale of 1.0 changes nothing, so don't show a line

        boolean pos = pct > 0;
        consumer.accept(
                Component.literal((pos ? "+" : "") + pct + "% ")
                        .append(Component.translatable(TerrarianReforging.CMP_NAME + ".tooltip." + key))
                        .withStyle(pos ? ChatFormatting.GREEN : ChatFormatting.DARK_RED));
    }

    public void forEachModifier(EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> out) {
        if(!quality.isBound()) return;
        Identifier id = quality.unwrapKey().map(ResourceKey::identifier).orElse(null);
        if(id == null) return;

        for(TRQuality.AttributeEntry ae : quality.value().modifiers()) {
            if(ae.slots().test(slot)) {
                out.accept(ae.attribute(), ae.toModifier(id));
            }
        }
    }
}
