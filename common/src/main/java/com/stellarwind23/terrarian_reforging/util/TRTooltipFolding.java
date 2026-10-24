package com.stellarwind23.terrarian_reforging.util;

import com.stellarwind23.terrarian_reforging.content.component.TRQuality;
import com.stellarwind23.terrarian_reforging.content.component.TRQualityComponent;
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

    private static double playerBase(Holder<Attribute> attr) {
        if (attr.is(Attributes.ATTACK_DAMAGE)) return 1.0;
        if (attr.is(Attributes.ATTACK_SPEED)) return 4.0;
        return 0.0;   // armor and toughness start at 0
    }

    private static boolean isWeaponBase(AttributeModifier mod) {
        return mod.is(Item.BASE_ATTACK_DAMAGE_ID) || mod.is(Item.BASE_ATTACK_SPEED_ID);
    }

    private static boolean isArmorBase(Holder<Attribute> attr, AttributeModifier mod) {
        return (attr.is(Attributes.ARMOR) || attr.is(Attributes.ARMOR_TOUGHNESS))
                && mod.operation() == AttributeModifier.Operation.ADD_VALUE
                && mod.id().getNamespace().equals("minecraft")
                && mod.id().getPath().startsWith("armor.");
    }

    private static boolean covers(EquipmentSlotGroup entryGroup, EquipmentSlotGroup shown) {
        if (entryGroup == shown) return true;
        return shown.slots().stream().allMatch(entryGroup::test);
    }

    public static TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> wrap(
            TRQualityComponent qc, EquipmentSlotGroup group,
            TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> out) {

        List<TRQuality.AttributeEntry> entries = qc.quality().value().modifiers().stream()
                .filter(e -> covers(e.slots(), group))
                .toList();
        if (entries.isEmpty()) return out;

        return (attr, mod, display) -> {
            boolean weaponBase = isWeaponBase(mod);
            boolean armorBase = isArmorBase(attr, mod);
            List<TRQuality.AttributeEntry> mine = entries.stream()
                    .filter(e -> e.attribute().equals(attr)).toList();

            if ((!weaponBase && !armorBase) || mine.isEmpty()) {   // every other vanilla line stays untouched
                out.accept(attr, mod, display);
                return;
            }

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

            Component name = Component.translatable(attr.value().getDescriptionId());
            Component line;

            if (armorBase) {
                // normal "+3 Armor" / "-1 Armor Toughness" style, aqua because it was modified
                String key = value < 0 ? "attribute.modifier.take.0" : "attribute.modifier.plus.0";
                line = Component.translatable(key,
                                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(Math.abs(value)), name)
                        .withStyle(ChatFormatting.AQUA);
            } else {
                // base weapon stats keep the " 8 Attack Damage" form, green when modified
                line = CommonComponents.space()
                        .append(Component.translatable("attribute.modifier.equals.0",
                                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(value), name))
                        .withStyle(ChatFormatting.GREEN);
            }

            out.accept(attr, mod, ItemAttributeModifiers.Display.override(line));
        };
    }
}