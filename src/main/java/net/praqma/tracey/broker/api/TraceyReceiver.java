package net.praqma.tracey.broker.api;

import java.util.concurrent.TimeoutException;

/**
 * <h2>The tracey receiver interface</h2>
 * <p>
 * The interface to implement for tracey to receive messages
 * </p>
 */
public interface TraceyReceiver <T extends RoutingInfo> {
    String receive(T data, String key) throws TraceyIOError, TimeoutException;
}
