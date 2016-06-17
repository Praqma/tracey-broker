package net.praqma.tracey.broker.rabbitmq;

import net.praqma.tracey.broker.TraceyBroker;
import com.rabbitmq.client.ConnectionFactory;
import java.io.File;

/**
 * <h2>Tracey broker for RabbitMQ</h2>
 * <p>
 * This class implements a simple broker for RabbitMQ messaging.
 * </p>
 * <p>
 * The default {@link TraceyRabbitMQReceiverImpl} does nothing more than print
 * the received message to std.out.
 * </p>
 */
public class TraceyRabbitMQBrokerImpl extends TraceyBroker<TraceyRabbitMQReceiverImpl, TraceyRabbitMQSenderImpl> {

    /**
     * An {@link  ExchangeType} representing the exchange types available for RabbitMQ.
     */
    public enum ExchangeType {
        FANOUT("fanout"),
        DIRECT("direct");

        private String type;

        ExchangeType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public TraceyRabbitMQBrokerImpl(TraceyRabbitMQReceiverImpl receiver, TraceyRabbitMQSenderImpl sender) {
        super(receiver, sender);
    }


    /**
     * Default constructor. Makes a very basic receiver and sender.
     */
    public TraceyRabbitMQBrokerImpl() {
        this.receiver = new TraceyRabbitMQReceiverBuilder()
                .setExchange("tracey")
                .setHost("localhost")
                .setType(ExchangeType.FANOUT).build();
        this.sender = new TraceyRabbitMQSenderImpl();
        sender.configure();
    }

    /**
     *
     * @param host  the host you wish to connect to for sending and receiving messages.
     */
    public TraceyRabbitMQBrokerImpl(String host) {
        this.receiver = new TraceyRabbitMQReceiverBuilder()
                .setExchange("tracey")
                .setHost(host)
                .setType(ExchangeType.FANOUT).build();
        receiver.configure();
        this.sender = new TraceyRabbitMQSenderImpl();
        sender.configure();
    }

    public TraceyRabbitMQBrokerImpl(File configFile) {
        TraceyRabbitMQReceiverBuilder builder = TraceyRabbitMQReceiverBuilder
                .buildFromConfigFile(configFile);
        this.receiver = builder.build();
        receiver.configure();

        this.sender = builder.buildSender();
        sender.configure();
    }

    /**
     * Full constructor
     *
     * @param host
     * @param password
     * @param user
     * @param type
     * @param exchange
     */
    public TraceyRabbitMQBrokerImpl(String host, String password, String user, ExchangeType type, String exchange) {
        this.receiver = new TraceyRabbitMQReceiverBuilder()
                .setHost(host)
                .setUsername(user)
                .setPassword(password)
                .setExchange(exchange)
                .setType(type).build();
        receiver.configure();
        this.sender = new TraceyRabbitMQSenderImpl();
        sender.setHost(host);
        sender.setPw(password);
        sender.setType(type);
        sender.setUsername(user);
        sender.configure();
    }
}
