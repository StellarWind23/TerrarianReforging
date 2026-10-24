package com.stellarwind23.terrarian_reforging.content.crafting;

import com.stellarwind23.terrarian_reforging.content.TRItems;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.List;

public final class TRTrades {
    private TRTrades() {}

    // 24 emeralds -> 1 token, one use per restock, Expert-level XP
    public static final VillagerTrades.ItemListing REFORGING_TOKEN =
            (level, trader, random) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 19),
                    new ItemStack(TRItems.REFORGING_TOKEN.get()),
                    3,       // max uses before the villager restocks
                    15,      // villager XP
                    0.05F);  // price multiplier from reputation

    public static final List<ResourceKey<VillagerProfession>> PROFESSIONS = List.of(
            VillagerProfession.TOOLSMITH, VillagerProfession.WEAPONSMITH, VillagerProfession.ARMORER);
}