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

    /** We share the connection factory */
    private ConnectionFactory factory = new ConnectionFactory();

    public TraceyRabbitMQBrokerImpl(TraceyRabbitMQReceiverImpl receiver, TraceyRabbitMQSenderImpl sender) {
        super(receiver, sender);
    }

    /**
     * Default constructor. Makes a very basic receiver and sender.
     */
    public TraceyRabbitMQBrokerImpl() {
        this.receiver = new TraceyRabbitMQReceiverBuilder()
                .setExchange("tracey")
                .setFactory(factory)
                .setHost("localhost")
                .setType(ExchangeType.FANOUT).build();
        this.sender = new TraceyRabbitMQSenderImpl(factory);
    }

    /**
     *
     * @param host  the host you wish to connect to for sending and receiving messages.
     */
    public TraceyRabbitMQBrokerImpl(String host) {
        this.receiver = new TraceyRabbitMQReceiverBuilder()
                .setExchange("tracey")
                .setFactory(factory)
                .setHost(host)
                .setType(ExchangeType.FANOUT).build();
        this.sender = new TraceyRabbitMQSenderImpl(factory);
    }

    public TraceyRabbitMQBrokerImpl(File configFile) {
        this.receiver = TraceyRabbitMQReceiverBuilder
                .buildFromConfigFile(configFile)
                .setFactory(factory).build();
        this.sender = new TraceyRabbitMQSenderImpl(factory);
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
}
