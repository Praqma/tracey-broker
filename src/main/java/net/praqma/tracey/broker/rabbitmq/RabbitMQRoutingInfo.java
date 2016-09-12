package net.praqma.tracey.broker.rabbitmq;

import groovy.util.ConfigObject;
import net.praqma.tracey.broker.RoutingInfo;
import net.praqma.tracey.core.TraceyDefaultParserImpl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
        final int deliveryMode = (int) m.getOrDefault("broker.rabbitmq.routingInfo.deliveryMode", RabbitMQDefaults.DELEIVERY_MODE);
        final String routingKey = (String) m.getOrDefault("broker.rabbitmq.routingInfo.routingKey", RabbitMQDefaults.ROUTING_KEY);
        final String exchangeName = (String) m.getOrDefault("broker.rabbitmq.routingInfo.exchangeName", RabbitMQDefaults.EXCHANGE_NAME);
        final String exchangeType = (String) m.getOrDefault("broker.rabbitmq.routingInfo.exchangeType", RabbitMQDefaults.EXCHANGE_TYPE.toString());
        final Map<String, Object> headers = extractHeaders(m);
        return new RabbitMQRoutingInfo(headers, deliveryMode , routingKey, exchangeName, exchangeType);
    }

    private static Map<String, Object> extractHeaders(Map<String, Object> config) {
        Map<String, Object> headers = new HashMap<>();
        config.forEach((key,value)->{
            if(key.contains("broker.rabbitmq.routingInfo.headers")){
                headers.put(key.replace("broker.rabbitmq.routingInfo.headers.", ""), value);
            }
        });
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public Map<String, Object> getHeaders() {
        return this.headers;
    }

    public int getDeliveryMode() {
        return this.deliveryMode;
    }

    public String getRoutingKey() {
        return this.routingKey;
    }

    public String getExchangeName() {
        return this.exchangeName;
    }

    public String getExchangeType() {
        return this.exchangeType;
    }

    public void setDeliveryMode(int deliveryMode) { this.deliveryMode = deliveryMode; }

    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }

    public void setExchangeName(String exchangeName) { this.exchangeName = exchangeName; }

    public void setExchangeType(String exchangeType) { this.exchangeType = exchangeType; }
}
