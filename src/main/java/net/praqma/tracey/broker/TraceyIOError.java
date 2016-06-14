package net.praqma.tracey.broker;

public class TraceyIOError extends Exception {
    public TraceyIOError(String message, Throwable innerCause) {
        super(message, innerCause);
    }
}
