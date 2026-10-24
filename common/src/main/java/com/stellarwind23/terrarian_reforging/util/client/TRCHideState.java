package com.stellarwind23.terrarian_reforging.util.client;

public final class TRCHideState {
    private static final ThreadLocal<Boolean> HIDE_QUALITY = ThreadLocal.withInitial(() -> false);

    private TRCHideState() {}

    public static boolean hideQuality() {
        return HIDE_QUALITY.get();
    }

    /** Sets the flag and returns the previous value, so callers can restore it (handles nesting). */
    public static boolean setHideQuality(boolean value) {
        boolean prev = HIDE_QUALITY.get();
        HIDE_QUALITY.set(value);
        return prev;
    }
}