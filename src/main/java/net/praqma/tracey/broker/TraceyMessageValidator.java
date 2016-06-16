package net.praqma.tracey.broker;

public interface TraceyMessageValidator {
    public void validateSend(String message) throws TraceyValidatorError;
    public void validateReceive(String message) throws TraceyValidatorError;
}
