package com.stellarwind23.terrarian_reforging.util;

import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.content.component.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.component.TRQuality;
import com.stellarwind23.terrarian_reforging.content.component.TRQualityComponent;
import com.stellarwind23.terrarian_reforging.content.event.TRQualityEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantable;

import java.util.List;
import java.util.Optional;

public final class TRQualityRoller {

    public static boolean accepts(ItemStack stack) { return stack.is(TerrarianReforging.ACCEPTED_EQUIPMENT);}

    private static List<Holder.Reference<TRQuality>> candidates(HolderLookup.Provider regs, ItemStack stack) {
        Optional<ResourceKey<TRQuality>> cur = Optional.ofNullable(
                stack.get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get())
        ).flatMap(c -> c.quality().unwrapKey());

        return regs.lookupOrThrow(TerrarianReforging.REG_KEY).listElements()
                .filter(h -> !cur.map(h::is).orElse(false))
                .filter(h -> h.value().applicableTo().contains(stack.getItemHolder()))
                .toList();
    }

    public static boolean hasCandidates(HolderLookup.Provider regs, ItemStack stack) {
        return !candidates(regs, stack).isEmpty();
    }

    public static Optional<Holder<TRQuality>> roll(HolderLookup.Provider regs, ItemStack stack, RandomSource rnd) {
        var pool = candidates(regs, stack);
        int total = pool.stream().mapToInt(h -> h.value().weight()).sum();
        if(total <= 0) return Optional.empty();

        int r = rnd.nextInt(total);
        for(Holder<TRQuality> h: pool) {
            r -= h.value().weight();
            if(r < 0) return Optional.of(h);
        }
        return Optional.empty();
    }

    public static void apply(ItemStack stack, Holder<TRQuality> q) {
        TRQualityComponent old = stack.get(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());
        stack.set(TRDataComponentTypes.TR_QUALITY_COMPONENT.get(), new TRQualityComponent(q));
        TRQualityEvents.QUALITY_APPLIED.invoker().onApplied(stack, q, old == null ? null : old.quality());
    }

    public static void tryRollNew(HolderLookup.Provider regs, ItemStack stack, RandomSource rnd) {
        if (stack.isEmpty() || !accepts(stack)) return;
        if (stack.has(TRDataComponentTypes.TR_QUALITY_COMPONENT.get())) return;   // keeps qualities a table set itself
        if (rnd.nextFloat() >= TerrarianReforging.FIRST_CRAFT_CHANCE) return;

        roll(regs, stack, rnd).ifPresent(q -> apply(stack, q));
    }

    public static void clear(ItemStack stack) {
        stack.remove(TRDataComponentTypes.TR_QUALITY_COMPONENT.get());

        DataComponentMap proto = stack.getItem().components();

        int base = proto.getOrDefault(DataComponents.MAX_DAMAGE, 0);
        if (base > 0) {
            int oldMax = stack.getMaxDamage();
            int damage = stack.getDamageValue();
            stack.set(DataComponents.MAX_DAMAGE, base);
            if (oldMax > 0) {
                stack.setDamageValue(Math.min(base - 1, Math.round((float) damage / oldMax * base)));
            }
        }

        Enchantable ench = proto.get(DataComponents.ENCHANTABLE);
        if (ench != null) stack.set(DataComponents.ENCHANTABLE, ench);
    }

    public static boolean supports(Holder<TRQuality> quality, ItemStack stack) {
        return quality.value().applicableTo().contains(stack.getItemHolder());
    }
}
