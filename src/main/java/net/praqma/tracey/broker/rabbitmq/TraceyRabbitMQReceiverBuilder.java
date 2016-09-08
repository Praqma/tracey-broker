package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import groovy.util.ConfigObject;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import net.praqma.tracey.core.TraceyDefaultParserImpl;

/**
 *
 * @author Mads
 */
public class TraceyRabbitMQReceiverBuilder {
    private String host = "localhost";
    private int port = 5672;
    private String username;
    private TraceyRabbitMQBrokerImpl.ExchangeType type = TraceyRabbitMQBrokerImpl.ExchangeType.FANOUT;
    private String exchange = "tracey";
    private ConnectionFactory factory = new ConnectionFactory();
    private TraceyRabbitMQMessageHandler handler;
    private String password;

    public TraceyRabbitMQReceiverImpl build() {
        return new TraceyRabbitMQReceiverImpl(expand(host), expand(exchange), type, expand(password), expand(username), port);
    }

    public static String expand(String original) {

        if(original == null)
            return null;

        String text = original;
        Map<String, String> envMap = System.getenv();
        for (Entry<String, String> entry : envMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            text = text.replace("${" + key + "}", value);
            text = text.replace("$"+key+"$", value);
            text = text.replace("$"+key, value);
            text = text.replace("%"+key+"%", value);
        }

        return text;
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
            String uName = getOrDefault((String)m.get("broker.rabbitmq.username"), "guest");
            String pWord = getOrDefault((String)m.get("broker.rabbitmq.password"), "guest");
            String pNumber = getOrDefault((String)m.get("broker.rabbitmq.port"), "5672");

            return new TraceyRabbitMQReceiverBuilder().
                    setHost(host).setExchange(exchange).
                    setPassword(pWord).
                    setPort(Integer.parseInt(pNumber)).
                    setUsername(uName).
                    setType(TraceyRabbitMQBrokerImpl.ExchangeType.valueOf(typeString.toUpperCase()));
        }
        return null;
    }

    public TraceyRabbitMQSenderImpl buildSender() {
        return new TraceyRabbitMQSenderImpl(expand(host), expand(username), expand(password), getPort());
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

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     * @return
     */
    public TraceyRabbitMQReceiverBuilder setPort(int port) {
        this.port = port;
        return this;
    }
}