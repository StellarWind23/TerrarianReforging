package com.stellarwind23.terrarian_reforging.util;

import com.stellarwind23.terrarian_reforging.content.TRQuality;
import com.stellarwind23.terrarian_reforging.content.TRQualityComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.List;

public final class TRTooltipFolding {

    // vanilla player base values; the tooltip adds these to the item's base modifier
    private static double playerBase(Holder<Attribute> attr) {
        if (attr.is(Attributes.ATTACK_DAMAGE)) return 1.0;
        if (attr.is(Attributes.ATTACK_SPEED)) return 4.0;
        return 0.0;
    }

    public static TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> wrap(
            TRQualityComponent qc, EquipmentSlotGroup group,
            TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> out) {

        List<TRQuality.AttributeEntry> entries = qc.quality().value().modifiers().stream()
                .filter(e -> e.slots() == group)
                .toList();
        if (entries.isEmpty()) return out;

        return (attr, mod, display) -> {
            boolean isBase = mod.is(Item.BASE_ATTACK_DAMAGE_ID) || mod.is(Item.BASE_ATTACK_SPEED_ID);
            List<TRQuality.AttributeEntry> mine = entries.stream()
                    .filter(e -> e.attribute().equals(attr)).toList();

            if (!isBase || mine.isEmpty()) {          // leave every other vanilla line untouched
                out.accept(attr, mod, display);
                return;
            }

            // same order of operations as the attribute system
            double sum = playerBase(attr) + mod.amount();
            double mulBase = 0, mulTotal = 1;
            for (var e : mine) {
                switch (e.operation()) {
                    case ADD_VALUE -> sum += e.amount();
                    case ADD_MULTIPLIED_BASE -> mulBase += e.amount();
                    case ADD_MULTIPLIED_TOTAL -> mulTotal *= 1 + e.amount();
                }
            }
            double value = sum * (1 + mulBase) * mulTotal;

            Component line = CommonComponents.space()
                    .append(Component.translatable("attribute.modifier.equals.0",
                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(value),
                            Component.translatable(attr.value().getDescriptionId())))
                    .withStyle(ChatFormatting.YELLOW);   // yellow = modified

            out.accept(attr, mod, ItemAttributeModifiers.Display.override(line));
        };
    }
}