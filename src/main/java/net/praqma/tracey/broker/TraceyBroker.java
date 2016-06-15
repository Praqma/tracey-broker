package net.praqma.tracey.broker;

import java.io.File;

public interface TraceyBroker {
    public String send(String payload, String destination) throws TraceyValidatorError, TraceyIOError;
    public String receive(String source);
    public void configure(File f);
}
