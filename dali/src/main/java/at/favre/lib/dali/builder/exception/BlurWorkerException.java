package at.favre.lib.dali.builder.exception;

/**
 * Created by PatrickF on 31.05.2014.
 */
public class BlurWorkerException extends RuntimeException {
    public BlurWorkerException() {
    }

    public BlurWorkerException(String detailMessage) {
        super(detailMessage);
    }

    public BlurWorkerException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BlurWorkerException(Throwable throwable) {
        super(throwable);
    }
}
