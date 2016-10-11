package net.praqma.tracey.broker.api;

/**
 * <h2>The tracey sender interface</h2>
 * <p>
 * The interface to implement for tracey to send messages
 * </p>
 */
public interface TraceySender <T extends RoutingInfo>{
    String send(String payload, T data) throws TraceyIOError;
}
