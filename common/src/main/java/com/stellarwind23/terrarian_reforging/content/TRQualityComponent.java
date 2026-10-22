package com.stellarwind23.terrarian_reforging.content;

import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record TRQualityComponent(
        TranslatableContents name,
        ItemAttributeModifiers modifiers
) {
}
