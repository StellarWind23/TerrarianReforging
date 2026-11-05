package com.stellarwind23.terrarian_reforging;

import com.stellarwind23.terrarian_reforging.content.component.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.TRItems;
import com.stellarwind23.terrarian_reforging.content.component.TRQuality;
import com.stellarwind23.terrarian_reforging.content.component.TRSharpenedComponent;
import com.stellarwind23.terrarian_reforging.content.crafting.TRRecipeSerializers;
import com.stellarwind23.terrarian_reforging.content.command.TRQualityCommand;
import com.stellarwind23.terrarian_reforging.content.event.TRQualityEvents;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.EntityEvent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TerrarianReforging {

    public static final String MOD_ID = "terrarian_reforging";
    public static final String CMP_NAME = "tr_equipment_modifier";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final float FIRST_CRAFT_CHANCE = 0.6F; // move to config later

    public static final TagKey<Item> ACCEPTED_EQUIPMENT = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(TerrarianReforging.MOD_ID, "accepted_equipment"));

    public static final TagKey<Attribute> CUSTOM_DESCRIPTOR = TagKey.create(
            Registries.ATTRIBUTE,
            ResourceLocation.fromNamespaceAndPath(TerrarianReforging.MOD_ID, "custom_descriptor"));


    public static final ResourceKey<Registry<TRQuality>> REG_KEY = ResourceKey.createRegistryKey(
            ResourceLocation.fromNamespaceAndPath(MOD_ID, CMP_NAME)
    );

    public static void init() {
        // Write common init code here.
        TRDataComponentTypes.init();
        TRItems.init();
        TRRecipeSerializers.init();

        CommandRegistrationEvent.EVENT.register((dispatcher, ctx, selection) -> TRQualityCommand.register(dispatcher, ctx));

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

        EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
            if (amount > 0 && source.getEntity() instanceof ServerPlayer p && source.getDirectEntity() == p) {
                ItemStack held = p.getMainHandItem();
                var type = TRDataComponentTypes.TR_SHARPENED_COMPONENT.get();
                var sh = held.get(type);
                if (sh != null) {
                    if (sh.uses() <= 1) held.remove(type);
                    else held.set(type, new TRSharpenedComponent(sh.uses() - 1));
                }
            }
            return EventResult.pass();
        });

        LOGGER.info("Terrarian Reforging Initialized! :3");
    }
}
