package com.stellarwind23.terrarian_reforging.event;

import com.stellarwind23.terrarian_reforging.content.TRQuality;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface TRQualityEvents {

    Event<QualityApplied> QUALITY_APPLIED = EventFactory.createLoop();

    @FunctionalInterface
    interface QualityApplied {
        void onApplied(ItemStack stack, Holder<TRQuality> quality, @Nullable Holder<TRQuality> previous);
    }
}