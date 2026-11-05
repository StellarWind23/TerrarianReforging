package com.stellarwind23.terrarian_reforging.util;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.stellarwind23.terrarian_reforging.content.component.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.component.TRQuality;
import com.stellarwind23.terrarian_reforging.content.component.TRQualityComponent;
import com.stellarwind23.terrarian_reforging.content.component.TRSharpenedComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class TRTooltipFolding {

    // imports: com.google.common.collect.Multimap, java.util.ArrayList, java.util.List

    private static double foldedValue(Holder<Attribute> attr, double baseAmount, List<TRQuality.AttributeEntry> mine) {
        double sum = playerBase(attr) + baseAmount;
        double mulBase = 0, mulTotal = 1;
        for (var e : mine) {
            switch (e.operation()) {
                case ADD_VALUE -> sum += e.amount();
                case ADD_MULTIPLIED_BASE -> mulBase += e.amount();
                case ADD_MULTIPLIED_TOTAL -> mulTotal *= 1 + e.amount();
            }
        }
        return sum * (1 + mulBase) * mulTotal;
    }

    public record BaseRow(Holder<Attribute> attr, AttributeModifier mod, double value, boolean modified, boolean weapon) {}

    //Neoforge has to be special :/
    public static List<BaseRow> foldBases(ItemStack stack, EquipmentSlotGroup group, Multimap<Holder<Attribute>, AttributeModifier> map) {
        List<TRQuality.AttributeEntry> entries = entriesFor(stack, group);
        if (entries.isEmpty()) return List.of();

        List<BaseRow> rows = new ArrayList<>();
        boolean any = false;
        for (Map.Entry<Holder<Attribute>, AttributeModifier> en : map.entries()) {
            Holder<Attribute> attr = en.getKey();
            AttributeModifier mod = en.getValue();
            boolean weapon = isWeaponBase(mod), armor = isArmorBase(attr, mod);
            if (!weapon && !armor) continue;

            List<TRQuality.AttributeEntry> mine = entries.stream()
                    .filter(e -> e.attribute().equals(attr)).toList();
            boolean modified = !mine.isEmpty();
            double value = modified ? foldedValue(attr, mod.amount(), mine) : playerBase(attr) + mod.amount();
            rows.add(new BaseRow(attr, mod, value, modified, weapon));
            any |= modified;
        }
        if (!any) return List.of();
        rows.forEach(r -> map.remove(r.attr(), r.mod()));
        return rows;
    }

    public static double playerBase(Holder<Attribute> attr) {
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
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (shown.test(slot) && !entryGroup.test(slot)) return false;
        }
        return true;
    }

    public static BiConsumer<Holder<Attribute>, AttributeModifier> wrap(
            ItemStack stack, EquipmentSlotGroup group,
            BiConsumer<Holder<Attribute>, AttributeModifier> out) {

        List<TRQuality.AttributeEntry> entries = entriesFor(stack, group);
        if (entries.isEmpty()) return out;

        return (attr, mod) -> {
            boolean weaponBase = isWeaponBase(mod);
            boolean armorBase = isArmorBase(attr, mod);
            List<TRQuality.AttributeEntry> mine = entries.stream()
                    .filter(e -> e.attribute().equals(attr)).toList();

            if ((!weaponBase && !armorBase) || mine.isEmpty()) {   // every other line stays untouched
                out.accept(attr, mod);
                return;
            }

            double value = foldedValue(attr, mod.amount(), mine);
            // vanilla adds the player's own base value back for weapon base lines, so subtract it here
            double amount = weaponBase ? value - playerBase(attr) : value;
            out.accept(attr, new AttributeModifier(mod.id(), amount, AttributeModifier.Operation.ADD_VALUE));
        };
    }

    private static List<TRQuality.AttributeEntry> entriesFor(ItemStack stack, EquipmentSlotGroup group) {
        List<TRQuality.AttributeEntry> list = new ArrayList<>();

        TRQualityComponent qc = stack.get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());
        if (qc != null && qc.quality().isBound()) {
            qc.quality().value().modifiers().ifPresent(mods ->
                    mods.stream().filter(e -> covers(e.slots(), group)).forEach(list::add));
        }

        if (stack.has(TRDataComponentTypes.TR_SHARPENED_COMPONENT.get()) && covers(EquipmentSlotGroup.MAINHAND, group)) {
            list.add(new TRQuality.AttributeEntry(Attributes.ATTACK_DAMAGE, TRSharpenedComponent.BONUS,
                    AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND));
        }
        return list;
    }
}