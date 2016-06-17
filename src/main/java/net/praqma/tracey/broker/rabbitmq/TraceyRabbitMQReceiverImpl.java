package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private ConnectionFactory c;
    private String host;
    private String exchange;
    private ExchangeType type = ExchangeType.FANOUT;
    private TraceyRabbitMQMessageHandler handler;

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
     * A more detailed constructor. Used in {@link TraceyRabbitMQReceiverBuilder}
     *
     * @param c  the {@link ConnectionFactory} to use
     * @param host  the RabbitMQ host
     * @param exchange  the exchange to connect to
     * @param type  the type of the exchange to use
     */
    public TraceyRabbitMQReceiverImpl(ConnectionFactory c, String host, String exchange, ExchangeType type) {
        this.c = c;
        this.host = host;
        this.exchange = exchange;
        this.type = type;
        this.handler = new TraceyRabbitMQMessageHandler() {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("[tracey] " + new String(body, "UTF-8"));
            }
        };
    }

    @Override
    public String receive(String source) {
        try {
            final Connection connection = c.newConnection();
            final Channel channel = connection.createChannel();
            String configuredExchange = source != null ? source : getExchange();
            channel.exchangeDeclare(configuredExchange, type.toString());
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, configuredExchange, "");

            System.out.println(" [tracey] Waiting for messages. To exit press CTRL+C");
            System.out.println(" [tracey] Version    : 2.0");
            System.out.println(" [tracey] Using queue: " + queueName);
            System.out.println(" [tracey] Exchange   : " + configuredExchange);
            System.out.println(" [tracey] Host       : " + getHost());

            Consumer c = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    handler.handleDelivery(consumerTag, envelope, properties, body);
                }
            };

            channel.basicConsume(queueName, false, c);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error while recieving", ex);
        }

        return "";
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

}
