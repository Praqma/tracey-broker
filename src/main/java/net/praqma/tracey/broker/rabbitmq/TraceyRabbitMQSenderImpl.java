package net.praqma.tracey.broker.rabbitmq;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rabbitmq.client.AMQP;
import net.praqma.tracey.broker.TraceyIOError;
import net.praqma.tracey.broker.TraceySender;

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
    public TraceyRabbitMQSenderImpl(RabbitMQConnection connection) {
        this.connection = connection;
    }

    @Override
    public String send(String payload, RabbitMQRoutingInfo info) throws TraceyIOError {
        try {
            connection.createChannel();
            connection.declareExchange(info.getExchangeName(), RabbitMQExchangeType.valueOf(info.getExchangeType()), true, true);
            final AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            builder.deliveryMode(info.getDeliveryMode());
            builder.headers(info.getHeaders());
            connection.getChannel().basicPublish(info.getExchangeName(), info.getRoutingKey(), builder.build(), payload.getBytes(Charset.forName("UTF-8")));
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
