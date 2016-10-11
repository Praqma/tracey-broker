package net.praqma.tracey.broker.impl.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.AMQP;
import net.praqma.tracey.broker.api.TraceyFilter;
import net.praqma.tracey.broker.api.TraceyIOError;
import net.praqma.tracey.broker.api.TraceyReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h2>Default RabbitMQ receiver implementation</h2>
 * <p>
 * Basic implementation for RabbitMQ. Prints received messages to console.
 * </p>
 */
public class TraceyRabbitMQReceiverImpl implements TraceyReceiver<RabbitMQRoutingInfo> {

    private static final Logger LOG = Logger.getLogger(TraceyRabbitMQReceiverImpl.class.getName());
    private final RabbitMQConnection connection;
    private final List<TraceyFilter> filters;
    // handlers will be set by calling tracey message handler and can not be final
    private TraceyRabbitMQMessageHandler handler;

    /**
     * A default constructor.
     */
    public TraceyRabbitMQReceiverImpl() {
        connection = new RabbitMQConnection();
        handler = new TraceyConsolePrintHandler();
        filters = Collections.emptyList();
    }

    /**
     * A detailed constructor.
     * @param connection RabbitMQ connection object
     */
    public TraceyRabbitMQReceiverImpl(final RabbitMQConnection connection, final List<TraceyFilter> filters) {
        this.connection = connection;
        this.handler = new TraceyConsolePrintHandler();
        this.filters = filterNulls(filters);
    }

    private static List<TraceyFilter> filterNulls(final List<TraceyFilter> filters) {
        if (filters == null) {
            return Collections.emptyList();
        }

        final List<TraceyFilter> list = new ArrayList<>(filters.size());
        for (TraceyFilter traceyFilter : list) {
            if (traceyFilter != null) {
                list.add(traceyFilter);
            }
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public String receive(final RabbitMQRoutingInfo info) throws TraceyIOError {
        try {
            final Channel channel = connection.createChannel();
            LOG.fine(String.format("Declare exchange: %s, type: %s, durable: true", info.getExchangeName(), info.getExchangeType()));
            channel.exchangeDeclare(info.getExchangeName(), info.getExchangeType(), true);
            final Set<String> routingKeys = new HashSet<>();

            for(TraceyFilter tf : filters) {
                routingKeys.addAll(tf.preReceive());
            }

            final String queueName = channel.queueDeclare().getQueue();

            if(routingKeys.isEmpty()) {
                channel.queueBind(queueName, info.getExchangeName(), "#");
            } else {
                for(String s : routingKeys) {
                    channel.queueBind(queueName, info.getExchangeName(), s);
                }
            }

            LOG.info(" [tracey] Host        : " + connection.getHost());
            LOG.info(" [tracey] Using queue    : " + queueName);
            LOG.info(" [tracey] Exchange       : " + info.getExchangeName());
            LOG.info(" [tracey] Routing key(s) :");

            if(routingKeys.isEmpty()) {
                LOG.info(" [tracey]  * #");
            }

            for(String s : routingKeys) {
                LOG.info(" [tracey]  * " + s);
            }

            LOG.info(" [tracey] Waiting for messages. To exit press CTRL+C");

            final Consumer c = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(final String consumerTag, final Envelope envelope, final AMQP.BasicProperties properties, final byte[] body) throws IOException {
                    handler.handleDelivery(consumerTag, envelope, properties, body);
                }

                @Override
                public void handleCancel(final String consumerTag) throws IOException {
                    super.handleCancel(consumerTag);
                    handler.handleCancel(consumerTag);
                }

                @Override
                public void handleShutdownSignal(final String consumerTag, final ShutdownSignalException sig) {
                    super.handleShutdownSignal(consumerTag, sig); //To change body of generated methods, choose Tools | Templates.
                    handler.handleShutdownSignal(consumerTag, sig);
                }

                @Override
                public void handleRecoverOk(final String consumerTag) {
                    LOG.info(String.format("Succesfully recovered connection for %s", consumerTag));
                }

            };

            return channel.basicConsume(queueName, false, c);

        } catch (IOException | TimeoutException ex) {
            LOG.log(Level.SEVERE, "Error while recieving", ex);
            throw new TraceyRabbitMQError("Exception caught while recieving using RabbitMQ", ex);
        }
    }

    public void cancel(final String consumerTag) throws IOException {
        if(consumerTag != null && connection.getChannel() != null) {
            try {
                connection.getChannel().basicCancel(consumerTag);
            } catch (AlreadyClosedException ignore) {
                LOG.info("Ignoring, connection was forcibly closed elsewhere");
            }
        }
    }

    public TraceyRabbitMQMessageHandler getHandler() {
        return handler;
    }

    public void setHandler(final TraceyRabbitMQMessageHandler handler) {
        this.handler = handler;
    }

    public List<TraceyFilter> getFilters() {
        return filters;
    }

    public RabbitMQConnection getConnection() {
        return connection;
    }
}
