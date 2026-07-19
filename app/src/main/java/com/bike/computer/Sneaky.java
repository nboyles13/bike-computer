package com.bike.computer;

/**
 * Helper to rethrow a caught Throwable without declaring it, restoring the
 * semantics of Kotlin's exception handling (no checked exceptions). Used where
 * the decompiler produced `try { throw t; } catch (Throwable tN) { ...; throw tN; }`
 * resource-close-and-rethrow blocks that Java cannot express directly.
 */
final class Sneaky {
    private Sneaky() {
    }

    @SuppressWarnings("unchecked")
    static <T extends Throwable> RuntimeException sneak(Throwable t) throws T {
        throw (T) t;
    }
}
