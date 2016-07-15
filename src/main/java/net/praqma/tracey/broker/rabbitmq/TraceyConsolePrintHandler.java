package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;

/**
 *
 * @author Mads
 */
public class TraceyConsolePrintHandler implements TraceyRabbitMQMessageHandler {

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("[tracey] " + new String(body, "UTF-8"));
    }

    @Override
    public void handleCancel(String consumerTag) throws IOException {
        System.out.println(String.format("[tracey] Consumer was cancelled %s", consumerTag));
    }

    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        System.out.println(String.format("[tracey] Consumer shut down: %s", consumerTag));
    }
}
