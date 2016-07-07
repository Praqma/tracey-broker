package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.praqma.tracey.broker.TraceyIOError;
import net.praqma.tracey.broker.TraceyReceiver;
import net.praqma.tracey.broker.rabbitmq.TraceyRabbitMQBrokerImpl.ExchangeType;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeCreatedEventOuterClass;

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
    private String exchange;
    private String password;
    private String username;
    private ExchangeType type = ExchangeType.FANOUT;
    private TraceyRabbitMQMessageHandler handler;
    private Channel channel;

    /** Default constructor */
    public TraceyRabbitMQReceiverImpl() {
        handler = new TraceyRabbitMQMessageHandler() {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("[tracey] " + new String(body, "UTF-8"));
            }
        };
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
    public TraceyRabbitMQReceiverImpl(String host, String exchange, ExchangeType type, String pw, String username) {
        this.host = host;
        this.exchange = exchange;
        this.username = username;
        this.type = type;
        this.password = pw;
        this.handler = new TraceyRabbitMQMessageHandler() {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("[tracey] " + new String(body, "UTF-8"));
            }
        };
    }

    public final void configure() {

        if (getPassword() != null) {
            factory.setPassword(getPassword());
        }

        if (getUsername() != null) {
            factory.setUsername(getUsername());
        }

        factory.setHost(getHost());
    }

    @Override
    public String receive(String source) throws TraceyIOError {
        try {
            channel = createChannel();
            String configuredExchange = source != null ? source : getExchange();

            TraceyEventTypeFilter filter = new TraceyEventTypeFilter(channel, exchange);
            filter.accept(EiffelSourceChangeCreatedEventOuterClass.EiffelSourceChangeCreatedEvent.class);
            filter.setAcceptAll(true);
            String qName = filter.apply();

            System.out.println(" [tracey] Using queue    : " + qName);
            System.out.println(" [tracey] Exchange       : " + configuredExchange);
            System.out.println(" [tracey] Routing key(s) : " + configuredExchange);
            for(String s : filter.routingKeys()) {
                System.out.println(" [tracey]  * "+s);
            }
            System.out.println(" [tracey] Host        : " + getHost());
            System.out.println(" [tracey] Waiting for messages. To exit press CTRL+C");

            Consumer c = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    handler.handleDelivery(consumerTag, envelope, properties, body);
                }

            };

            return channel.basicConsume(qName, false, c);

        } catch (IOException | TimeoutException ex) {
            LOG.log(Level.SEVERE, "Error while recieving", ex);
            throw new TraceyRabbitMQError("Exception caught while recieving using RabbitMQ", ex);
        }
    }

    public void cancel(String consumerTag) throws IOException {
        if(consumerTag != null && channel != null) {
            channel.basicCancel(consumerTag);
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
}
