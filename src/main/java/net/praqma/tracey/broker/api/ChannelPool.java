package net.praqma.tracey.broker.api;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Empty interface to have an abstraction to implement a channel handling
 */
public interface ChannelPool {
    Channel createChannel(String key) throws IOException, TimeoutException;
    Channel getChannel(String key);
    void closeChannel(String key, String consumerTag) throws IOException, TimeoutException;
}
