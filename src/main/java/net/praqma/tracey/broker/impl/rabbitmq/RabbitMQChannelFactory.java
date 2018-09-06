package net.praqma.tracey.broker.impl.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.praqma.tracey.broker.api.ChannelFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 */
public class RabbitMQChannelFactory implements ChannelFactory {

    private Connection connection;

    public RabbitMQChannelFactory(ConnectionFactory factory) throws IOException, TimeoutException {

        connection = factory.newConnection();
    }

    @Override
    public Channel createChannel() throws IOException, TimeoutException {
        connection.createChannel();
        return null;
    }
}
