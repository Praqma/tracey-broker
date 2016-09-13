package net.praqma.tracey.broker.rabbitmq;

import net.praqma.tracey.broker.TraceyBroker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
     * Default constructor. Creates receiver and sender using defaults.
     */
    public TraceyRabbitMQBrokerImpl() {
        this.receiver = new TraceyRabbitMQReceiverImpl();
        this.sender = new TraceyRabbitMQSenderImpl();
    }

    /**
     * A full constructor
     * @param receiver RabbitMQ receiver object
     * @param sender   RabbitMQ sender object
     */
    public TraceyRabbitMQBrokerImpl(final TraceyRabbitMQReceiverImpl receiver, final TraceyRabbitMQSenderImpl sender) {
        super(receiver, sender);
    }

    /**
     * A full constructor
     * @param connection RabbitMQ connection object
     * @param filters    List of filters for receiver
     */
    public TraceyRabbitMQBrokerImpl(final RabbitMQConnection connection, final List<TraceyFilter> filters) {
        this.receiver = new TraceyRabbitMQReceiverImpl(connection, filters);
        this.sender = new TraceyRabbitMQSenderImpl(connection);
    }

    /**
     * Create all objects using parameters from config file. Filters won't be set for receiver. Set them afterwards
     * @param configFile File object with config file
     */
    public TraceyRabbitMQBrokerImpl(final File configFile) {
        final RabbitMQConnection connection = RabbitMQConnection.buildFromConfigFile(configFile);
        this.receiver = new TraceyRabbitMQReceiverImpl(connection, new ArrayList<>());
        this.sender = new TraceyRabbitMQSenderImpl(connection);
    }

    public TraceyRabbitMQSenderImpl getSender() { return sender; }

    public TraceyRabbitMQReceiverImpl getReceiver() {
        return receiver;
    }
}
