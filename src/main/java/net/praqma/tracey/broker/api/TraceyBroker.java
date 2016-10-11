package net.praqma.tracey.broker.api;

/**
 * <h2>Tracey Broker</h2>
 * <p>
 * We have made the broker so that each component can be split out.
 * The receiver and the sender might be used separately. Or combined into the
 * broker which can validate sent and received messages.
 * </p>
 *
 * @author Praqma
 * @param <T>  the type of receiver this broker uses.
 * @param <S>  the type of sender this broker uses.
 */
public abstract class TraceyBroker<T extends TraceyReceiver, S extends TraceySender> {

    protected T receiver;
    protected S sender;

    public TraceyBroker() { }

    /**
     *
     * @param receiver  initialize the {@link TraceyBroker} with this {@link TraceyReceiver}
     * @param sender initialize the {@link TraceyBroker} with this {@link TraceySender}
     */
    public TraceyBroker(final T receiver, final S sender) {
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     *
     * @param payload  the payload you want to send using the {@link TraceySender}
     * @return the sent payload
     * @throws TraceyIOError if the chosen middleware for message sending encounters a network error
     */
    public String send(final String payload, final RoutingInfo data) throws TraceyIOError {
        return sender.send(payload, data);
    }

    /**
     *
     * @param data  the abstract representation of routing info what we get from broker and want to receive (listen). Receive can be blocking.
     * @return the received message
     * @throws TraceyIOError if the chosen middleware for receiving messages encounters a network error.
     */
    public String receive(final RoutingInfo data) throws TraceyIOError {
        return receiver.receive(data);
    }

    /**
     * @return the receiver
     */
    public T getReceiver() {
        return receiver;
    }

    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(final T receiver) {
        this.receiver = receiver;
    }

    /**
     * @return the sender
     */
    public S getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(final S sender) {
        this.sender = sender;
    }
}
