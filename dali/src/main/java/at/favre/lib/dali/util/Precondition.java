package at.favre.lib.dali.util;

/**
 * Created by PatrickF on 30.05.2014.
 */
public class Precondition {
	private Precondition() {}

	public static void checkNotNull(String messageIfNull, Object obj) {
		if(obj == null) {
			throw new IllegalArgumentException(messageIfNull);
		}
	}

	public static void checkArgument(String messageIfFalse, boolean condition) {
		if(!condition) {
			throw new IllegalArgumentException(messageIfFalse);
		}
	}
}
