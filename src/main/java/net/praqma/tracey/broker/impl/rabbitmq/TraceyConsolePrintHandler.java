package net.praqma.tracey.broker.impl.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;

public class TraceyConsolePrintHandler implements TraceyRabbitMQMessageHandler {

    @Override
    public void handleDelivery(final String consumerTag, final Envelope envelope, final AMQP.BasicProperties properties, final byte[] body) throws IOException {
        System.out.println("[tracey] " + new String(body, "UTF-8"));
    }

    @Override
    public void handleCancel(final String consumerTag) throws IOException {
        System.out.println(String.format("[tracey] Consumer was cancelled %s", consumerTag));
    }

    @Override
    public void handleShutdownSignal(final String consumerTag, final ShutdownSignalException sig) {
        System.out.println(String.format("[tracey] Consumer shut down: %s", consumerTag));
    }
}
