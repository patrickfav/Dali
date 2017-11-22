package at.favre.lib.dali.util;

public final class Precondition {
    private Precondition() {
    }

    public static void checkNotNull(String messageIfNull, Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException(messageIfNull);
        }
    }

    public static void checkArgument(String messageIfFalse, boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException(messageIfFalse);
        }
    }
}
