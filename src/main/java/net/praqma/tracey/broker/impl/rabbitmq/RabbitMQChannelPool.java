package net.praqma.tracey.broker.impl.rabbitmq;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import net.praqma.tracey.broker.api.ChannelFactory;
import net.praqma.tracey.broker.api.ChannelPool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 *
 */
class RabbitMQChannelPool implements ChannelPool {
    private static final Logger LOG = Logger.getLogger(RabbitMQChannelPool.class.getName());
    private Map<String, Channel> channelPool;
    private ChannelFactory channelFactory;

    RabbitMQChannelPool(ChannelFactory channelFactory) {
        channelPool = new HashMap<>();
        this.channelFactory = channelFactory;
    }

    @Override
    public Channel createChannel(String key) throws IOException, TimeoutException {
        Channel channel = channelFactory.createChannel();
        Channel existedChannel = channelPool.put(key, channel);
        if (existedChannel != null) {
            existedChannel.close();
        }
        return channel;
    }
    @Override
    public Channel getChannel(String key) {
        return channelPool.get(key);
    }

    @Override
    public void closeChannel(final String jobName, final String consumerTag) throws IOException, TimeoutException {
        if(consumerTag != null && channelPool.get(jobName) != null) {
            try {
                channelPool.get(jobName).basicCancel(consumerTag);
            } catch (AlreadyClosedException ignore) {
                LOG.info("Ignoring, connection was forcibly closed elsewhere");
            }
        }
    }


}
