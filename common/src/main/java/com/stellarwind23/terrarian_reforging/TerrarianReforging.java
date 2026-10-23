package com.stellarwind23.terrarian_reforging;

import com.stellarwind23.terrarian_reforging.content.TRDataComponentTypes;
import com.stellarwind23.terrarian_reforging.content.TRItems;
import com.stellarwind23.terrarian_reforging.content.TRQuality;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public final class TerrarianReforging {

    public static final String MOD_ID = "terrarian_reforging";
    public static final String CMP_NAME = "tr_quality";

    public static final ResourceKey<Registry<TRQuality>> REG_KEY = ResourceKey.createRegistryKey(
            Identifier.fromNamespaceAndPath(MOD_ID, CMP_NAME)
    );

    public static void init() {
        // Write common init code here.
        TRDataComponentTypes.init();
        TRItems.init();
    }
}
