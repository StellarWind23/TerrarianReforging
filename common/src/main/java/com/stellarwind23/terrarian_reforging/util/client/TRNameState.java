package com.stellarwind23.terrarian_reforging.util.client;

import java.util.function.Supplier;

public final class TRNameState {
    private static final ThreadLocal<Boolean> BYPASS = ThreadLocal.withInitial(() -> false);
    private TRNameState() {}

    public static boolean bypass() { return BYPASS.get(); }

    public static <T> T withoutQuality(Supplier<T> action) {
        boolean prev = BYPASS.get();
        BYPASS.set(true);
        try { return action.get(); } finally { BYPASS.set(prev); }
    }
}