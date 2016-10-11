package net.praqma.tracey.broker.impl.rabbitmq;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import net.praqma.tracey.broker.api.TraceyIOError;
import net.praqma.tracey.broker.api.TraceySender;

/**
 * <h2>Tracey RabbitMQ sender</h2>
 * <p>
 * Basic implementation. Very simple.
 * </p>
 */
public class TraceyRabbitMQSenderImpl implements TraceySender <RabbitMQRoutingInfo> {

    private static final Logger LOG = Logger.getLogger(TraceyRabbitMQSenderImpl.class.getName());
    private RabbitMQConnection connection;

    /**
     * A default constructor.
     */
    public TraceyRabbitMQSenderImpl() {
        this.connection = new RabbitMQConnection();
    }

    /**
     * A detailed constructor.
     * @param connection RabbitMQ connection object
     */
    public TraceyRabbitMQSenderImpl(final RabbitMQConnection connection) {
        this.connection = connection;
    }

    @Override
    public String send(final String payload, final RabbitMQRoutingInfo info) throws TraceyIOError {
        try {
            final Channel channel = connection.createChannel();
            LOG.fine(String.format("Declare exchange: %s, type: %s, durable: true", info.getExchangeName(), info.getExchangeType()));
            channel.exchangeDeclare(info.getExchangeName(), info.getExchangeType(), true);
            final AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            builder.deliveryMode(info.getDeliveryMode());
            builder.headers(info.getHeaders());
            channel.basicPublish(info.getExchangeName(), info.getRoutingKey(), builder.build(), payload.getBytes(Charset.forName("UTF-8")));
            connection.closeChannel();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "IOException", ex);
            throw new TraceyIOError("TraceyIOError", ex);
        } catch (TimeoutException ex) {
            LOG.log(Level.SEVERE, "Timeout occured", ex);
            throw new TraceyIOError("Tracey send timed out", ex);
        }
        return payload;
    }

    public RabbitMQConnection getConnection() { return this.connection; }
}
