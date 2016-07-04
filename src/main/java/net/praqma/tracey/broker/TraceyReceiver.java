package net.praqma.tracey.broker;

/**
 * <h2>The tracey receiver interface</h2>
 * <p>
 * The interface to implement for tracey to receive messages
 * </p>
 */
public interface TraceyReceiver {
    public String receive(String source) throws TraceyIOError;
}
