package net.praqma.tracey.broker;

/**
 * <h2>The tracey sender interface</h2>
 * <p>
 * The interface to implement for tracey to send messages
 * </p>
 */
public interface TraceySender {
    public String send(String payload, String destination) throws TraceyValidatorError, TraceyIOError;
}
