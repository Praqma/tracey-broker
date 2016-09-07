package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.Channel;
import net.praqma.tracey.broker.TraceyMessageData;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface TraceyMessageDispatcher {
    public void dispatch(Channel channel, String destination, TraceyMessageData data, byte[] payload) throws IOException, TimeoutException;
}
