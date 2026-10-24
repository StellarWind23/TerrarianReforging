package com.stellarwind23.terrarian_reforging;

import com.stellarwind23.terrarian_reforging.content.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.TRItems;
import com.stellarwind23.terrarian_reforging.content.TRQuality;
import com.stellarwind23.terrarian_reforging.content.TRRecipeSerializers;
import com.stellarwind23.terrarian_reforging.event.TRQualityEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TerrarianReforging {

    public static final String MOD_ID = "terrarian_reforging";
    public static final String CMP_NAME = "tr_quality";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final float FIRST_CRAFT_CHANCE = 0.6F; // move to config later

    public static final TagKey<Item> ACCEPTED_EQUIPMENT = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(TerrarianReforging.MOD_ID, "accepted_equipment"));


    public static final ResourceKey<Registry<TRQuality>> REG_KEY = ResourceKey.createRegistryKey(
            Identifier.fromNamespaceAndPath(MOD_ID, CMP_NAME)
    );

    public static void init() {
        // Write common init code here.
        TRDataComponentTypes.init();
        TRItems.init();
        TRRecipeSerializers.init();

        TRQualityEvents.QUALITY_APPLIED.register((stack, quality, previous) -> {
            int base = stack.getItem().components().getOrDefault(DataComponents.MAX_DAMAGE, 0);
            if (base <= 0) return; // not a damageable item

            int oldMax = stack.getMaxDamage();
            int damage = stack.getDamageValue();

            int newMax = Math.max(1, Math.round(base * quality.value().durabilityScale().orElse(1F)));
            stack.set(DataComponents.MAX_DAMAGE, newMax);

            // keep the same fraction worn, so reforging doesn't heal or break the item
            if (oldMax > 0) {
                stack.setDamageValue(Math.min(newMax - 1, Math.round((float) damage / oldMax * newMax)));
            }
        });
    }
}
