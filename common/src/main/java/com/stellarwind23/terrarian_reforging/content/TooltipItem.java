package com.stellarwind23.terrarian_reforging.content;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;

public class TooltipItem extends Item {
    private final List<Component> lines;

    public TooltipItem(Properties props, Component... lines) {
        super(props);
        this.lines = List.of(lines);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NonNull ItemStack stack, Item.@NonNull TooltipContext ctx, @NonNull TooltipDisplay display, @NonNull Consumer<Component> out, @NonNull TooltipFlag flag) {
        super.appendHoverText(stack, ctx, display, out, flag);
        lines.forEach(out);
    }
}