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
    private String username;
    private TraceyRabbitMQBrokerImpl.ExchangeType type = TraceyRabbitMQBrokerImpl.ExchangeType.FANOUT;
    private String exchange = "tracey";
    private ConnectionFactory factory = new ConnectionFactory();
    private TraceyRabbitMQMessageHandler handler;
    private String password;

    public TraceyRabbitMQReceiverImpl build() {
        return new TraceyRabbitMQReceiverImpl(host, exchange, type, password, username);
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
            String host = getOrDefault((String) m.get("broker.rabbitmq.host"), "localhost");
            String exchange = getOrDefault((String) m.get("broker.rabbitmq.exchange"), "tracey");
            String typeString = getOrDefault((String) m.get("broker.rabbitmq.type"), "fanout");
            String uName = (String) m.get("broker.rabbitmq.username");
            String pWord = (String) m.get("broker.rabbitmq.password");
            return new TraceyRabbitMQReceiverBuilder().
                    setHost(host).setExchange(exchange).
                    setPassword(pWord).
                    setUsername(uName).
                    setType(TraceyRabbitMQBrokerImpl.ExchangeType.valueOf(typeString.toUpperCase()));
        }
        return null;
    }

    public TraceyRabbitMQSenderImpl buildSender() {
        return new TraceyRabbitMQSenderImpl(host, username, password);
    }


    public static String getOrDefault(String value, String defa) {
        if(value != null) {
            return value;
        }
        return defa;
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

    /**
     * @return the pw
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param pw the pw to set
     * @return
     */
    public TraceyRabbitMQReceiverBuilder setPassword(String pw) {
        this.password = pw;
        return this;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     * @return
     */
    public TraceyRabbitMQReceiverBuilder setUsername(String username) {
        this.username = username;
        return this;
    }
}
