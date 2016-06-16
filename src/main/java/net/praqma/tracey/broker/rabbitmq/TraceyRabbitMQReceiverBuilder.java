/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import groovy.util.ConfigObject;
import java.io.File;
import java.util.Map;
import net.praqma.tracey.core.TraceyDefaultParserImpl;

/**
 *
 * @author Mads
 */
public class TraceyRabbitMQReceiverBuilder {
    private String host = "localhost";
    private TraceyRabbitMQBrokerImpl.ExchangeType type = TraceyRabbitMQBrokerImpl.ExchangeType.FANOUT;
    private String exchange = "tracey";
    private ConnectionFactory factory;
    private TraceyRabbitMQMessageHandler handler;

    public TraceyRabbitMQReceiverImpl build() {
        return new TraceyRabbitMQReceiverImpl(getFactory(), getHost(), getExchange(), getType());
    }

    /**
     * <p>
     * Parses a config file in this form: broker { rabbitmq { host = 'localhost'
     * durable = true exchange = '' queue = '' } }
     * </p>
     *
     * @param f - The file to parse. Currently uses the built in config slurper for
     *            groovy
     * @return  - A mostly configured builder. Pass in the ConnectionFactory
     */
    public static TraceyRabbitMQReceiverBuilder buildFromConfigFile(File f) {
        if (f != null && f.exists()) {
            TraceyDefaultParserImpl parser = new TraceyDefaultParserImpl();
            Map m = ((ConfigObject) parser.parse(f)).flatten();
            String host = (String) m.get("broker.rabbitmq.host");
            String exchange = (String) m.get("broker.rabbitmq.exchange");
            String typeString = (String) m.get("broker.rabbitmq.type");
            return new TraceyRabbitMQReceiverBuilder().
                    setHost(host).setExchange(exchange).
                    setType(TraceyRabbitMQBrokerImpl.ExchangeType.valueOf(typeString));
        }
        return null;
    }

    public String getHost() {
        return host;
    }

    public TraceyRabbitMQReceiverBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public TraceyRabbitMQBrokerImpl.ExchangeType getType() {
        return type;
    }

    public TraceyRabbitMQReceiverBuilder setType(TraceyRabbitMQBrokerImpl.ExchangeType type) {
        this.type = type;
        return this;
    }

    public String getExchange() {
        return exchange;
    }

    public TraceyRabbitMQReceiverBuilder setExchange(String exchange) {
        this.exchange = exchange;
        return this;
    }

    /**
     * @return the factory
     */
    public ConnectionFactory getFactory() {
        return factory;
    }

    public TraceyRabbitMQReceiverBuilder setFactory(ConnectionFactory factory) {
        this.factory = factory;
        return this;
    }

    /**
     * @return the handler
     */
    public TraceyRabbitMQMessageHandler getHandler() {
        return handler;
    }

    /**
     * @param handler the handler to set
     */
    public void setHandler(TraceyRabbitMQMessageHandler handler) {
        this.handler = handler;
    }
}
