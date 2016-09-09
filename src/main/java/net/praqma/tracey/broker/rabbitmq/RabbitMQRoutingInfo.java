package net.praqma.tracey.broker.rabbitmq;

import net.praqma.tracey.broker.RoutingInfo;

import java.util.Map;

/**
 * Interface for routing info
 * Created by alexandrasedova on 07/09/16.
 */
public class RabbitMQRoutingInfo implements RoutingInfo {
    private Map<String, Object> headers;
    private int deliveryMode;
    private String routingKey;
    private String exchangeName;
    private String exchangeType;

    public RabbitMQRoutingInfo(Map<String, Object> headers, String exchangeName, int deliveryMode, String routingKey) {
        this.headers = headers;
        this.exchangeName = exchangeName;
        this.deliveryMode = deliveryMode;
        this.routingKey = routingKey;
    }

    public RabbitMQRoutingInfo(Map<String, Object> headers, int deliveryMode, String routingKey, String exchangeName, String exchangeType) {
        this.headers = headers;
        this.deliveryMode = deliveryMode;
        this.routingKey = routingKey;
        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public int getDeliveryMode() {
        return deliveryMode;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getExchangeType() {
        return exchangeType;
    }

}
