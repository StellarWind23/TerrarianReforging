package com.stellarwind23.terrarian_reforging.neoforge;

import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.TerrarianReforgingClient;
import com.stellarwind23.terrarian_reforging.content.component.TRQuality;
import com.stellarwind23.terrarian_reforging.content.crafting.TRTrades;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
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

       modBus.addListener((VillagerTradesEvent e) -> {
            if (TRTrades.PROFESSIONS.contains(e.getType())) {
                e.getTrades().get(4).add(TRTrades.REFORGING_TOKEN);
            }
        });

        modBus.addListener((FMLClientSetupEvent e) -> {
            TerrarianReforgingClient.init();
        });
        // Run our common setup.
        TerrarianReforging.init();
    }
}
