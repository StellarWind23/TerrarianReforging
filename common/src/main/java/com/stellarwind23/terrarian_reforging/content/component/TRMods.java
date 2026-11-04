package com.stellarwind23.terrarian_reforging.content.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import dev.architectury.platform.Platform;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public final class TRMods {
    public static final MapCodec<List<String>> FIELD =
            Codec.list(ExtraCodecs.NON_EMPTY_STRING).optionalFieldOf("required_mods", List.of());

    public static <T> DataResult<Boolean> satisfied(DynamicOps<T> ops, T input) {
        return ops.getMap(input)
                .flatMap(map -> FIELD.decode(ops, map))
                .map(mods -> mods.stream().allMatch(Platform::isModLoaded));
    }
    private TRMods() {}
}