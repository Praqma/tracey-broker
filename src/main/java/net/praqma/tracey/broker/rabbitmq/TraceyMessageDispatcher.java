package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.Channel;
import net.praqma.tracey.broker.RoutingInfo;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface TraceyMessageDispatcher <T extends RoutingInfo>{
    public void dispatch(Channel channel, T data, byte[] payload) throws IOException, TimeoutException;
}
