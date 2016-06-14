package net.praqma.tracey.broker;

public interface TraceyMessageValidator {
    public void validate(String message) throws TraceyValidatorError;
}
