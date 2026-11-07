package com.stellarwind23.terrarian_reforging.neoforge;

import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.TerrarianReforgingClient;
import com.stellarwind23.terrarian_reforging.config.TerrarianReforgingConfig;
import com.stellarwind23.terrarian_reforging.content.component.TRQuality;
import eu.midnightdust.lib.config.MidnightConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@Mod(TerrarianReforging.MOD_ID)
public final class TerrarianReforgingNeoForge {
    public TerrarianReforgingNeoForge(IEventBus modBus) {
        // Run our common setup.
        TerrarianReforging.init();

        //Setup dynamic registry
        modBus.addListener((DataPackRegistryEvent.NewRegistry e) -> {
            e.dataPackRegistry(TerrarianReforging.REG_KEY,
                    TRQuality.DIRECT_CODEC,     //Datapack Load
                    TRQuality.DIRECT_CODEC);    //Client Sync
        });

        modBus.addListener((FMLClientSetupEvent e) -> {
            TerrarianReforgingClient.init();
        });

        modBus.addListener((FMLCommonSetupEvent e) -> {
            MidnightConfig.init(TerrarianReforging.MOD_ID, TerrarianReforgingConfig.class);
        });
    }
}
