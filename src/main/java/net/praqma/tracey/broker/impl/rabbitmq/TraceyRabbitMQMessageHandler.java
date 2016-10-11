package net.praqma.tracey.broker.impl.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;

/**
 * <h2>Message handler</h2>
 * <p>
 * Simplistic interface to implement if you do not wish to write a new {@link TraceyRabbitMQReceiverImpl}
 * </p>
 */
public interface TraceyRabbitMQMessageHandler {
    void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, final byte[] body) throws IOException;
    void handleCancel(String consumerTag) throws IOException;
    void handleShutdownSignal(String consumerTag, ShutdownSignalException sig);
}
