package net.praqma.tracey.broker;

/**
 * <h2>Tracey validation error</h2>
 * <p>
 * This error is thrown when the {@link TraceyMessageValidator} fails to validate
 * a message
 * </p>
 */
public class TraceyValidatorError extends Exception {
    public TraceyValidatorError(String message) {
        super(message);
    }
}
