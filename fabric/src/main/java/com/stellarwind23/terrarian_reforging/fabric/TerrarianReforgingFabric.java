package com.stellarwind23.terrarian_reforging.fabric;

import com.stellarwind23.terrarian_reforging.TerrarianReforging;
import com.stellarwind23.terrarian_reforging.content.component.TRQuality;
import com.stellarwind23.terrarian_reforging.content.crafting.TRTrades;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;

public final class TerrarianReforgingFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        //Setup dynamic registry
        DynamicRegistries.registerSynced(TerrarianReforging.REG_KEY, TRQuality.DIRECT_CODEC);

        TerrarianReforging.init();

        for (var profession : TRTrades.PROFESSIONS) {
            TradeOfferHelper.registerVillagerOffers(profession, 4, factories -> factories.add(TRTrades.REFORGING_TOKEN));
        }
    }
}
