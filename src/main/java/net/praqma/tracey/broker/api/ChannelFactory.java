package net.praqma.tracey.broker.api;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface ChannelFactory {

    public Channel createChannel() throws IOException, TimeoutException;

}
