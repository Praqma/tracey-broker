package net.praqma.tracey.broker.rabbitmq;

import net.praqma.tracey.broker.TraceyBroker;
import com.rabbitmq.client.ConnectionFactory;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class TraceyRabbitMQBrokerImpl extends TraceyBroker<TraceyRabbitMQReceiverImpl, TraceyRabbitMQSenderImpl> {

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

    private static final Logger LOG = Logger.getLogger(TraceyRabbitMQBrokerImpl.class.getName());
    private ConnectionFactory factory = new ConnectionFactory();

    public TraceyRabbitMQBrokerImpl(TraceyRabbitMQReceiverImpl receiver, TraceyRabbitMQSenderImpl sender) {
        super(receiver, sender);
    }

    public TraceyRabbitMQBrokerImpl() throws IOException, TimeoutException {
        this.receiver = new TraceyRabbitMQReceiverBuilder()
                .setExchange("tracey")
                .setFactory(factory)
                .setHost("localhost")
                .setType(ExchangeType.FANOUT).build();
        this.sender = new TraceyRabbitMQSenderImpl(factory);
    }

    public TraceyRabbitMQBrokerImpl(String host) throws IOException, TimeoutException {
        this.receiver = new TraceyRabbitMQReceiverBuilder()
                .setExchange("tracey")
                .setFactory(factory)
                .setHost(host)
                .setType(ExchangeType.FANOUT).build();
        this.sender = new TraceyRabbitMQSenderImpl(factory);
    }

    public TraceyRabbitMQBrokerImpl(File configFile) throws IOException, TimeoutException {
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
