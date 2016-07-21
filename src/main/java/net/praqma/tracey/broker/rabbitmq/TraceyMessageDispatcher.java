package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface TraceyMessageDispatcher {
    public void dispatch(Channel channel, String destination, byte[] payload) throws IOException, TimeoutException;
}
