package net.praqma.tracey.broker.api;

/**
 * <h2>TraceyIOError</h2>
 * <p>
 * Exception that is thrown when the chosen middleware encounters a general network
 * error.
 * </p>
 * <p>
 * Look for the real cause in the Exception payload.
 * </p>
 */
public class TraceyIOError extends Exception {
    public TraceyIOError(String message, Throwable innerCause) {
        super(message, innerCause);
    }
}
