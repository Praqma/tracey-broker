package net.praqma.tracey.broker.impl.rabbitmq;

/**
 * An {@link  RabbitMQExchangeType} representing the exchange types available for RabbitMQ.
 */
public enum RabbitMQExchangeType {
    FANOUT("fanout"),
    TOPIC("topic"),
    HEADERS("headers"),
    DIRECT("direct");


    private String type;

    RabbitMQExchangeType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
