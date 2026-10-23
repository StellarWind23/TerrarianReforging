package com.stellarwind23.terrarian_reforging.neoforge;

import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.content.TRQuality;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@Mod(TerrarianReforging.MOD_ID)
public final class TerrarianReforgingNeoForge {
    public TerrarianReforgingNeoForge(IEventBus modBus) {
        //Setup dynamic registry
        modBus.addListener((DataPackRegistryEvent.NewRegistry e) -> {
            e.dataPackRegistry(TerrarianReforging.REG_KEY,
                    TRQuality.DIRECT_CODEC,     //Datapack Load
                    TRQuality.DIRECT_CODEC);    //Client Sync
        });
        // Run our common setup.
        TerrarianReforging.init();
    }
}
