package com.stellarwind23.terrarian_reforging.util;

import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.content.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.TRQuality;
import com.stellarwind23.terrarian_reforging.content.TRQualityComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

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
        stack.set(TRDataComponentTypes.TR_QUALITY_COMPONENT.get(), new TRQualityComponent(q));
    }
}
