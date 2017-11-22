package at.favre.lib.dali.builder.exception;

/**
 * Created by PatrickF on 31.05.2014.
 */
public class LiveBlurWorkerException extends RuntimeException {
    public LiveBlurWorkerException() {
    }

    public LiveBlurWorkerException(String detailMessage) {
        super(detailMessage);
    }

    public LiveBlurWorkerException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public LiveBlurWorkerException(Throwable throwable) {
        super(throwable);
    }
}
