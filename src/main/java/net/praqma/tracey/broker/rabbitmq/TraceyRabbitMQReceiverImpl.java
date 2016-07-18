package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
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
import net.praqma.tracey.broker.rabbitmq.TraceyRabbitMQBrokerImpl.ExchangeType;

/**
 * <h2>Default RabbitMQ receiver implementation</h2>
 * <p>
 * Basic implementation for RabbitMQ. Prints received messages to console.
 * </p>
 */
public class TraceyRabbitMQReceiverImpl implements TraceyReceiver {

    private static final Logger LOG = Logger.getLogger(TraceyRabbitMQReceiverImpl.class.getName());
    private ConnectionFactory factory = new ConnectionFactory();
    private String host;
    private int port;
    private String exchange;
    private String password;
    private String username;
    private ExchangeType type = ExchangeType.FANOUT;
    private TraceyRabbitMQMessageHandler handler;
    private Channel channel;
    private List<TraceyFilter> filters = new ArrayList<>();

    /** Default constructor */
    public TraceyRabbitMQReceiverImpl() {
        handler = new TraceyConsolePrintHandler();
    }

    /**
     *
     * @return the channel to be used by this reciever. We want to later on attach
     *         maybe more than 1 consumer.
     * @throws IOException when transport error occurs
     * @throws TimeoutException when the we fail to connect because of timeout
     */
    public Channel createChannel() throws IOException, TimeoutException {
        if(channel != null)
            return channel;
        this.channel = factory.newConnection().createChannel();
        return channel;
    }

    /**
     * A more detailed constructor. Used in {@link TraceyRabbitMQReceiverBuilder}

     * @param host  the RabbitMQ host
     * @param exchange  the exchange to connect to
     * @param type  the type of the exchange to us
     * @param pw  password for the broker
     * @param username
     */
    public TraceyRabbitMQReceiverImpl(String host, String exchange, ExchangeType type, String pw, String username, int port) {
        this.host = host;
        this.exchange = exchange;
        this.username = username;
        this.type = type;
        this.port = port;
        this.password = pw;
        this.handler = new TraceyConsolePrintHandler();
    }

    public final void configure() {

        if (getPassword() != null) {
            factory.setPassword(getPassword());
        }

        if (getUsername() != null) {
            factory.setUsername(getUsername());
        }
        factory.setPort(getPort());
        factory.setHost(getHost());
        factory.setAutomaticRecoveryEnabled(true);
    }

    @Override
    public String receive(String source) throws TraceyIOError {
        try {
            configure();
            channel = createChannel();
            String configuredExchange = source != null ? source : getExchange();

            Set<String> routingKeys = new HashSet<>();

            for(TraceyFilter tf : getFilters()) {
                routingKeys.addAll(tf.preReceive());
            }

            channel.exchangeDeclare(configuredExchange, ExchangeType.TOPIC.toString());
            String queueName = channel.queueDeclare().getQueue();

            if(routingKeys.isEmpty()) {
                channel.queueBind(queueName, configuredExchange, "#");
            } else {
                for(String s : routingKeys) {
                    channel.queueBind(queueName, configuredExchange, s);
                }
            }

            System.out.println(" [tracey] Using queue    : " + queueName);
            System.out.println(" [tracey] Exchange       : " + configuredExchange);
            System.out.println(" [tracey] Routing key(s) :");

            if(routingKeys.isEmpty()) {
                System.out.println(" [tracey]  * #");
            }

            for(String s : routingKeys) {
                System.out.println(" [tracey]  * "+s);
            }

            System.out.println(" [tracey] Host        : " + getHost());
            System.out.println(" [tracey] Waiting for messages. To exit press CTRL+C");

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
        if(consumerTag != null && channel != null) {
            try {
                channel.basicCancel(consumerTag);
            } catch (AlreadyClosedException ignore) {
                LOG.info("Ignoring, connection was forcibly closed elsewhere");
            }
        }
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the exchange
     */
    public String getExchange() {
        return exchange;
    }

    /**
     * @param exchange the exchange to set
     */
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    /**
     * @return the type
     */
    public ExchangeType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ExchangeType type) {
        this.type = type;
    }

    /**
     * @return the handler
     */
    public TraceyRabbitMQMessageHandler getHandler() {
        return handler;
    }

    /**
     * @param handler the handler to set
     */
    public void setHandler(TraceyRabbitMQMessageHandler handler) {
        this.handler = handler;
    }

    /**
     * @return the pw
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the pw to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the factory
     */
    public ConnectionFactory getFactory() {
        return factory;
    }

    /**
     * @param factory the factory to set
     */
    public void setFactory(ConnectionFactory factory) {
        this.factory = factory;
    }

    /**
     * @return the channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * @param channel the channel to set
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the filters
     */
    public List<TraceyFilter> getFilters() {
        return filters;
    }

    /**
     * @param filters the filters to set
     */
    public void setFilters(List<TraceyFilter> filters) {
        this.filters = filters;
    }

    @Override
    public String toString() {
        return String.format("%s@%s:%s", factory.getUsername(), factory.getHost(), factory.getPort());
    }

}
