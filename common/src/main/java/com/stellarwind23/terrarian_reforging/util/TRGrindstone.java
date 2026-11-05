package com.stellarwind23.terrarian_reforging.util;

import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.config.TerrarianReforgingConfig;
import com.stellarwind23.terrarian_reforging.content.TRItems;
import com.stellarwind23.terrarian_reforging.content.component.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.component.TRSharpenedComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class TRGrindstone {
    public enum Kind { NONE, CLEANSE, SHARPEN }
    public static final TagKey<Item> SHARPENABLE = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(TerrarianReforging.MOD_ID, "sharpenable"));
    public static final ThreadLocal<Boolean> NO_XP = ThreadLocal.withInitial(() -> false);

    private static boolean isTool(ItemStack s) {
        return s.is(TRItems.COPPER_WOOL.get()) || s.is(TRItems.WHETSTONE.get());
    }

    public static Kind kind(ItemStack a, ItemStack b) {
        if (isTool(a) == isTool(b)) return Kind.NONE;      // exactly one tool
        ItemStack tool = isTool(a) ? a : b, target = isTool(a) ? b : a;
        if (target.getCount() != 1) return Kind.NONE;

        if (tool.is(TRItems.COPPER_WOOL.get()))
            return target.has(TRDataComponentTypes.TR_QUALITY_COMPONENT.get()) ? Kind.CLEANSE : Kind.NONE;

        var sh = target.get(TRDataComponentTypes.TR_SHARPENED_COMPONENT.get());
        int uses = sh == null ? 0 : sh.uses();
        return target.is(SHARPENABLE) && uses < TerrarianReforgingConfig.sharpening_uses ? Kind.SHARPEN : Kind.NONE;
    }

    public static int toolSlot(ItemStack a, ItemStack b) { return isTool(a) ? 0 : 1; }

    public static ItemStack result(Kind kind, ItemStack a, ItemStack b) {
        ItemStack r = (isTool(a) ? b : a).copy();
        switch (kind) {
            case CLEANSE -> TRQualityRoller.clear(r);
            case SHARPEN -> r.set(TRDataComponentTypes.TR_SHARPENED_COMPONENT.get(),
                    new TRSharpenedComponent(TerrarianReforgingConfig.sharpening_uses));
            default -> {}
        }
        return r;
    }
}