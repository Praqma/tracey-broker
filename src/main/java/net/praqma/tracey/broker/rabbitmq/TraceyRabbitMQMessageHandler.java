package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import java.io.IOException;

/**
 * <h2>Message handler</h2>
 * <p>
 * Simplistic interface to implement if you do not wish to write a new {@link TraceyRabbitMQReceiverImpl}
 * </p>
 */
public interface TraceyRabbitMQMessageHandler {
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, final byte[] body) throws IOException;
}
