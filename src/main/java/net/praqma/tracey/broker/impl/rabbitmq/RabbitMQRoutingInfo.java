package net.praqma.tracey.broker.impl.rabbitmq;

import groovy.util.ConfigObject;
import net.praqma.tracey.broker.api.RoutingInfo;
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

    /**
     * A default constructor.
     */
    public RabbitMQRoutingInfo() {
        this.headers = new HashMap<>();
        this.exchangeName = RabbitMQDefaults.EXCHANGE_NAME;
        this.exchangeType = RabbitMQDefaults.EXCHANGE_TYPE;
        this.deliveryMode = RabbitMQDefaults.DELEIVERY_MODE;
        this.routingKey = RabbitMQDefaults.ROUTING_KEY;
    }

    /**
     * A detailed constructor.
     * @param headers headers for outgoing messages
     * @param deliveryMode delvery mode for outgoing messages
     * @param routingKey routing key to use for outgoing messages
     * @param exchangeName exchange name to connect to
     * @param exchangeType exchange type to create if given exchange name doesn't exist
     */
    public RabbitMQRoutingInfo(final Map<String, Object> headers, final int deliveryMode, final String routingKey, final String exchangeName, final String exchangeType) {
        this.headers = headers;
        this.deliveryMode = deliveryMode;
        this.routingKey = routingKey;
        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;
    }

    /**
     * Read configuration file and create RabbitMQRoutingInfo object.
     * If some filed are not present in provided configuration file then default value from RabbitMQDefaults will be used
     * @param f File object that contains configuration file
     * @return  RabbitMQRoutingInfo object
     */
    public static RabbitMQRoutingInfo buildFromConfigFile(final File f) {
        final TraceyDefaultParserImpl parser = new TraceyDefaultParserImpl();
        final Map m = ((ConfigObject) parser.parse(f)).flatten();
        final int deliveryMode = (int) m.getOrDefault("broker.rabbitmq.routingInfo.deliveryMode", RabbitMQDefaults.DELEIVERY_MODE);
        final String routingKey = (String) m.getOrDefault("broker.rabbitmq.routingInfo.routingKey", RabbitMQDefaults.ROUTING_KEY);
        final String exchangeName = (String) m.getOrDefault("broker.rabbitmq.routingInfo.exchangeName", RabbitMQDefaults.EXCHANGE_NAME);
        final String exchangeType = (String) m.getOrDefault("broker.rabbitmq.routingInfo.exchangeType", RabbitMQDefaults.EXCHANGE_TYPE);
        final Map<String, Object> headers = extractHeaders(m);
        return new RabbitMQRoutingInfo(headers, deliveryMode , routingKey, exchangeName, exchangeType);
    }

    /**
     * Extract map with headers from flattened configuration file. Path to headers in configuration will look like
     * broker.rabbitmq.routingInfo.headers.somekey. We need to strip out broker.rabbitmq.routingInfo.headers. part
     * and only leave key value pairs
     * @param config flattened configuration file
     * @return  map with the headers to send
     */
    private static Map<String, Object> extractHeaders(final Map<String, Object> config) {
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
