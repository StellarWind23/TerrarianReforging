package com.stellarwind23.terrarian_reforging.content.component;

import com.mojang.serialization.Codec;
import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.util.TRTooltipColors;
import com.stellarwind23.terrarian_reforging.util.client.TRHideReforgeState;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record TRQualityComponent(Holder<TRQuality> quality) implements TooltipProvider {

    public static final Codec<TRQualityComponent> CODEC =
            TRQuality.HOLDER_CODEC.xmap(TRQualityComponent::new, TRQualityComponent::quality);

    public static final StreamCodec<RegistryFriendlyByteBuf, TRQualityComponent> STREAM_CODEC =
            TRQuality.STREAM_CODEC.map(TRQualityComponent::new,TRQualityComponent::quality);


    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        printLines(consumer);
    }

    public int printLines(Consumer<Component> consumer) {
        if (TRHideReforgeState.hideQuality()) return 0;

        TRQuality q = quality.value();

        int[] printed = {0};
        Consumer<Component> out = c -> { printed[0]++; consumer.accept(c); };

        for (TRQuality.AttributeEntry ae : q.modifiers().orElse(new ArrayList<>())) {
            out.accept(formatLine(ae));
        }

        q.durabilityScale().ifPresent(s -> scaleLine("durability", s, out));
        q.enchantability().ifPresent(b -> {
            if (b == 0) return;
            out.accept(Component.literal((b > 0 ? "+" : "") + b + " ")
                    .append(Component.translatable("tr_equipment_modifier.tooltip.enchantability"))
                    .withStyle(b > 0 ? TRTooltipColors.POSITIVE_IMPORTANT : TRTooltipColors.NEGATIVE_IMPORTANT));
        });

        return printed[0];
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
        return line.copy().withStyle(pos ? TRTooltipColors.POSITIVE : TRTooltipColors.NEGATIVE);
    }

    private static String customKey(Holder<Attribute> attr) {
        ResourceLocation id = attr.unwrapKey().map(ResourceKey::location).orElseThrow();
        return Util.makeDescriptionId(TerrarianReforging.CMP_NAME + ".attribute", id);
    }

    private static void scaleLine(String key, float scale, Consumer<Component> consumer) {
        int pct = Math.round((scale - 1F) * 100F);
        if (pct == 0) return; // a scale of 1.0 changes nothing, so don't show a line

        boolean pos = pct > 0;
        consumer.accept(
                Component.literal((pos ? "+" : "") + pct + "% ")
                        .append(Component.translatable(TerrarianReforging.CMP_NAME + ".tooltip." + key))
                        .withStyle(pos ? TRTooltipColors.POSITIVE_IMPORTANT : TRTooltipColors.NEGATIVE_IMPORTANT));
    }

    public void forEachModifier(EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> out) {
        if(!quality.isBound()) return;
        ResourceLocation id = quality.unwrapKey().map(ResourceKey::location).orElse(null);
        if(id == null) return;

        var m = quality.value().modifiers().orElse(new ArrayList<>());
        for(TRQuality.AttributeEntry ae : m) {
            if(ae.slots().test(slot)) {
                out.accept(ae.attribute(), ae.toModifier(id));
            }
        }
    }
}
