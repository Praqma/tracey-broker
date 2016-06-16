package net.praqma.tracey.broker;

public interface TraceySender {
    public String send(String payload, String destination) throws TraceyValidatorError, TraceyIOError;
}
