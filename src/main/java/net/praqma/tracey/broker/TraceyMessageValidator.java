package net.praqma.tracey.broker;

/**
 * <h2>Tracey Message Validation</h2>
 * <p>
 * This interface should be implemented and attached to your broker if you wish to perform some form of basic validation. 
 * </p>
 */
public interface TraceyMessageValidator {
    public void validateSend(String message) throws TraceyValidatorError;
    public void validateReceive(String message) throws TraceyValidatorError;
}
