package com.stellarwind23.terrarian_reforging.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class TerrarianReforgingConfig extends MidnightConfig {

    @Entry(min = 0, max = 1, isSlider = true) public static double loot_table_modifier_chance = 0.6;
    @Entry(min = 0, max = 1, isSlider = true) public static double crafting_modifier_chance = 0.6;
    @Entry(min = 0, max = 1, isSlider = true) public static double trading_modifier_chance = 0.6;
    @Entry(min = 1) public static int sharpening_uses = 35;
}
