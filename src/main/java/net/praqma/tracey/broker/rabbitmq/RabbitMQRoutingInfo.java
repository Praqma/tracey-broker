package net.praqma.tracey.broker.rabbitmq;

import groovy.util.ConfigObject;
import net.praqma.tracey.broker.RoutingInfo;
import net.praqma.tracey.core.TraceyDefaultParserImpl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RabbitMQRoutingInfo implements RoutingInfo {
    private Map<String, Object> headers;
    private int deliveryMode;
    private String routingKey;
    private String exchangeName;
    private String exchangeType;

    public RabbitMQRoutingInfo() {
        this.headers = new HashMap<>();
        this.exchangeName = RabbitMQDefaults.EXCHANGE_NAME;
        this.exchangeType = RabbitMQDefaults.EXCHANGE_TYPE.toString();
        this.deliveryMode = RabbitMQDefaults.DELEIVERY_MODE;
        this.routingKey = RabbitMQDefaults.ROUTING_KEY;
    }

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

    public static RabbitMQRoutingInfo buildFromConfigFile(File f) {
        final TraceyDefaultParserImpl parser = new TraceyDefaultParserImpl();
        final Map m = ((ConfigObject) parser.parse(f)).flatten();
        final int deliveryMode = (int) getValueOrDefault("broker.rabbitmq.routingInfo.deliveryMode", m, RabbitMQDefaults.DELEIVERY_MODE);
        final String routingKey = (String) getValueOrDefault("broker.rabbitmq.routingInfo.routingKey", m, RabbitMQDefaults.ROUTING_KEY);
        final String exchangeName = (String) getValueOrDefault("broker.rabbitmq.routingInfo.exchangeName", m, RabbitMQDefaults.EXCHANGE_NAME);
        final String exchangeType = (String) getValueOrDefault("broker.rabbitmq.routingInfo.exchangeType", m, RabbitMQDefaults.EXCHANGE_TYPE);
        final Map<String, Object> headers = (Map<String, Object>) getValueOrDefault("broker.rabbitmq.routingInfo.headers", m, RabbitMQDefaults.HEADERS);
        return new RabbitMQRoutingInfo(headers, deliveryMode , routingKey, exchangeName, exchangeType);
    }

    // TODO: move to tracey core TraceyDefaultParserImpl
    private static Object getValueOrDefault(final String key, final Map<String, Object> map, final Object defaultValue) {
        return Optional.ofNullable(map.get(key)).orElse(defaultValue);
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
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
