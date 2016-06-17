package net.praqma.tracey.broker;

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
    protected TraceyMessageValidator validator;

    /**
     *
     * @param payload  the payload you want to send using the {@link TraceySender}
     * @param destination  an abstract notation on where the payload goes
     * @return the sent payload
     * @throws TraceyValidatorError if the {@link TraceyMessageValidator} failed to validate the message that needs to be sent
     * @throws TraceyIOError if the chosen middleware for message sending encounters a network error
     */
    public String send(String payload, String destination) throws TraceyValidatorError, TraceyIOError {
        if(validator != null) {
            validator.validateSend(payload);
        }
        return sender.send(payload, destination);
    }

    /**
     *
     * @param destination  the abstract representation of what we want to receive (listen). Receive can be blocking.
     * @return the received message
     * @throws TraceyValidatorError if the {@link TraceyMessageValidator} failed to validate the received message
     * @throws TraceyIOError if the chosen middleware for receiving messages encounters a network error.
     */
    public String receive(String destination) throws TraceyValidatorError, TraceyIOError {
        if(validator != null) {
            validator.validateReceive(destination);
        }
        return receiver.receive(destination);
    }

    public TraceyBroker() { }

    /**
     *
     * @param receiver  initialize the {@link TraceyBroker} with this {@link TraceyReceiver}
     * @param sender initialize the {@link TraceyBroker} with this {@link TraceySender}
     */
    public TraceyBroker(T receiver, S sender) {
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * @return the validator
     */
    public TraceyMessageValidator getValidator() {
        return validator;
    }

    /**
     * @param validator the validator to set
     */
    public void setValidator(TraceyMessageValidator validator) {
        this.validator = validator;
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
    public void setReceiver(T receiver) {
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
    public void setSender(S sender) {
        this.sender = sender;
    }
}
