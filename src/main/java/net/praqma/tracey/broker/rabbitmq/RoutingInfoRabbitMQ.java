package net.praqma.tracey.broker.rabbitmq;

import net.praqma.tracey.broker.RoutingInfo;

import java.util.Map;

/**
 * Interface for routing info
 * Created by alexandrasedova on 07/09/16.
 */
public class RoutingInfoRabbitMQ implements RoutingInfo {
    private Map<String, Object> headers;
    private int deliveryMode;
    private String routingKey;
    private String destination; // Exchange name
    private String exchangeType;

    public RoutingInfoRabbitMQ(Map<String, Object> headers, String destination, int deliveryMode, String routingKey) {
        this.headers = headers;
        this.destination = destination;
        this.deliveryMode = deliveryMode;
        this.routingKey = routingKey;
    }

    public RoutingInfoRabbitMQ(Map<String, Object> headers, int deliveryMode, String routingKey, String destination, String exchangeType) {
        this.headers = headers;
        this.deliveryMode = deliveryMode;
        this.routingKey = routingKey;
        this.destination = destination;
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

    public String getDestination() {
        return destination;
    }

    public String getExchangeType() {
        return exchangeType;
    }

}
