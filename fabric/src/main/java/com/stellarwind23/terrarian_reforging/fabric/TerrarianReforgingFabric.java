package com.stellarwind23.terrarian_reforging.fabric;

import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.content.TRQuality;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

public final class TerrarianReforgingFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        //Setup dynamic registry
        DynamicRegistries.registerSynced(TerrarianReforging.REG_KEY, TRQuality.DIRECT_CODEC);

        TerrarianReforging.init();
    }
}
