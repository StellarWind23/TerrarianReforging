package com.stellarwind23.terrarian_reforging;

import com.stellarwind23.terrarian_reforging.content.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.TRItems;
import com.stellarwind23.terrarian_reforging.content.TRQuality;
import com.stellarwind23.terrarian_reforging.content.TRRecipeSerializers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class TerrarianReforging {

    public static final String MOD_ID = "terrarian_reforging";
    public static final String CMP_NAME = "tr_quality";

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
    }
}
