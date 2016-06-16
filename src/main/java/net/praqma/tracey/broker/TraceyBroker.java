package net.praqma.tracey.broker;

import java.io.File;

/**
 * <h2>General purpose broker for tracey</h2>
 * <p>
 * We have made the broker so that each component can be split out.
 * The receiver and the sender might be used separately. Or combined into the
 * broker which can validate sent and received messages.
 * </p>
 *
 * @author Praqma
 * @param <T> The type of receiver this broker uses.
 * @param <S> The type of sender this broker uses.
 */
public abstract class TraceyBroker<T extends TraceyReceiver, S extends TraceySender> {

    protected T receiver;
    protected S sender;
    protected TraceyMessageValidator validator;

    public String send(String payload, String destination) throws TraceyValidatorError, TraceyIOError {
        if(validator != null) {
            validator.validateSend(payload);
        }
        return sender.send(payload, destination);
    }

    public String receive(String destination) throws TraceyValidatorError, TraceyIOError {
        if(validator != null) {
            validator.validateReceive(destination);
        }
        return receiver.receive(destination);
    }

    public TraceyBroker() { }

    //Tracey with preconfigured sender and reciever
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
