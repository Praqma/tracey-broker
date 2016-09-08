package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.praqma.tracey.broker.TraceyIOError;
import net.praqma.tracey.broker.TraceyReceiver;

/**
 * <h2>Default RabbitMQ receiver implementation</h2>
 * <p>
 * Basic implementation for RabbitMQ. Prints received messages to console.
 * </p>
 */
public class TraceyRabbitMQReceiverImpl implements TraceyReceiver <RabbitMQRoutingInfo> {

    private static final Logger LOG = Logger.getLogger(TraceyRabbitMQReceiverImpl.class.getName());
    private RabbitMQConnection connection;
    private TraceyRabbitMQMessageHandler handler;
    private List<TraceyFilter> filters;

    /**
     * A default constructor.
     */
    public TraceyRabbitMQReceiverImpl() {
        this.connection = new RabbitMQConnection(RabbitMQDefaults.HOST,
                RabbitMQDefaults.PORT,
                RabbitMQDefaults.USERNAME,
                RabbitMQDefaults.PASSWORD,
                RabbitMQDefaults.AUTOMATIC_RECOVERY
        );
        handler = new TraceyConsolePrintHandler();
        filters = new ArrayList<>();
    }

    /**
     * A detailed constructor.
     * @param connection RabbitMQ connection object
     */
    public TraceyRabbitMQReceiverImpl(final RabbitMQConnection connection, final List<TraceyFilter> filters) {
        this.connection = connection;
        this.handler = new TraceyConsolePrintHandler();
        this.filters = filters;
    }

    @Override
    public String receive(RabbitMQRoutingInfo info) throws TraceyIOError {
        try {
            connection.createChannel();
            connection.declareExchange(info.getExchangeName(), RabbitMQExchangeType.valueOf(info.getExchangeType()), true, false);
            Channel channel = connection.getChannel();
            Set<String> routingKeys = new HashSet<>();

            for(TraceyFilter tf : filters) {
                routingKeys.addAll(tf.preReceive());
            }

            String queueName = channel.queueDeclare().getQueue();

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

            Consumer c = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    handler.handleDelivery(consumerTag, envelope, properties, body);
                }

                @Override
                public void handleCancel(String consumerTag) throws IOException {
                    super.handleCancel(consumerTag);
                    handler.handleCancel(consumerTag);
                }

                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                    super.handleShutdownSignal(consumerTag, sig); //To change body of generated methods, choose Tools | Templates.
                    handler.handleShutdownSignal(consumerTag, sig);
                }

                @Override
                public void handleRecoverOk(String consumerTag) {
                    LOG.info(String.format("Succesfully recovered connection for %s", consumerTag));
                }

            };

            return channel.basicConsume(queueName, false, c);

        } catch (IOException | TimeoutException ex) {
            LOG.log(Level.SEVERE, "Error while recieving", ex);
            throw new TraceyRabbitMQError("Exception caught while recieving using RabbitMQ", ex);
        }
    }

    public void cancel(String consumerTag) throws IOException {
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

    public void setHandler(TraceyRabbitMQMessageHandler handler) {
        this.handler = handler;
    }

    public List<TraceyFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<TraceyFilter> filters) {
        this.filters = filters;
    }

    public RabbitMQConnection getConnection() {
        return connection;
    }
}
