package net.praqma.tracey.broker.impl.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import groovy.util.ConfigObject;
import net.praqma.tracey.core.TraceyDefaultParserImpl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * Wrapper around RabbitMQ connection/connection factory class
 */
public class RabbitMQConnection {
    private static final Logger LOG = Logger.getLogger(RabbitMQConnection.class.getName());
    private final ConnectionFactory factory = new ConnectionFactory();
    private Channel channel;

    /**
     * A default constructor.
     */
    public RabbitMQConnection() {
        this.factory.setHost(RabbitMQDefaults.HOST);
        this.factory.setPort(RabbitMQDefaults.PORT);
        this.factory.setUsername(RabbitMQDefaults.USERNAME);
        this.factory.setPassword(RabbitMQDefaults.PASSWORD);
        this.factory.setAutomaticRecoveryEnabled(RabbitMQDefaults.AUTOMATIC_RECOVERY);
    }

    /**
     * A detailed constructor.
     * @param host the RabbitMQ host
     * @param port port for RabbitMQ server
     * @param userName The user name to use while creating connections to RabbitMQ
     * @param password password for the broker
     * @param automaticRecovery enable automatic recovery of the connection
     */
    public RabbitMQConnection(final String host, final int port, final String userName, final String password, final Boolean automaticRecovery) {
        this.factory.setHost(host);
        this.factory.setPort(port);
        this.factory.setUsername(userName);
        this.factory.setPassword(password);
        this.factory.setAutomaticRecoveryEnabled(automaticRecovery);
    }

    /**
     * Open connection and create a channel for further communication.
     * If channel was already created then return it
     *
     * @return the channel to be used by this connection
     * @throws IOException when transport error occurs
     * @throws TimeoutException when the we fail to connect because of timeout
     */
    public Channel createChannel() throws IOException, TimeoutException {
        if(channel != null) {
            return channel;
        }
        channel = factory.newConnection().createChannel();
        return channel;
    }

    /**
     * Close currently open channel. If channel is closed then do nothing
     *
     * @throws IOException when transport error occurs
     * @throws TimeoutException when the we fail to connect because of timeout
     */
    public void closeChannel() throws IOException, TimeoutException {
        if(channel != null) {
            channel.close();
            channel = null;
        }
    }

    /**
     * Read configuration file and create RabbitMQConnection object.
     * If some fileds are not present in provided configuration file then default value from RabbitMQDefaults will be used.
     * userName and passoword fields support environment variable expansion, i.e. you can use env variable name instead of
     * actual value. The following formats supported - $SOMETEXT, ${SOMETEXT}, $SOMETEXT$, %SOMETEXT%
     * @param f File object that contains configuration file
     * @return  RabbitMQConnection object
     */
    public static RabbitMQConnection buildFromConfigFile(final File f) {
        final TraceyDefaultParserImpl parser = new TraceyDefaultParserImpl();
        final Map m = ((ConfigObject) parser.parse(f)).flatten();
        final String host = (String) m.getOrDefault("broker.rabbitmq.connection.host", RabbitMQDefaults.HOST);
        final int port = (int) m.getOrDefault("broker.rabbitmq.connection.port", RabbitMQDefaults.PORT);
        final String userName = (String) m.getOrDefault("broker.rabbitmq.connection.userName", RabbitMQDefaults.USERNAME);
        final String password = (String) m.getOrDefault("broker.rabbitmq.connection.password", RabbitMQDefaults.PASSWORD);
        final boolean automaticRecovery = (Boolean) m.getOrDefault("broker.rabbitmq.connection.automaticRecovery", RabbitMQDefaults.AUTOMATIC_RECOVERY);
        return new RabbitMQConnection(host , port, expand(userName), expand(password), automaticRecovery);
    }

    // TODO: move to tracey-core to avoid duplication here and in RabbitMQRoutingInfo
    private static String expand(final String original) {
        if(original == null)
            return null;
        String text = original;
        final Map<String, String> envMap = System.getenv();
        for (Map.Entry<String, String> entry : envMap.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            text = text.replace("${" + key + "}", value);
            text = text.replace("$"+key+"$", value);
            text = text.replace("$"+key, value);
            text = text.replace("%"+key+"%", value);
        }
        return text;
    }

    public Channel getChannel() { return channel; }

    public String getHost() { return factory.getHost(); }

    public int getPort() { return factory.getPort(); }

    public String getUserName() { return factory.getUsername(); }

    public String getPassword() { return factory.getPassword(); }

    public Boolean isAutomaticRecoveryEnabled() { return factory.isAutomaticRecoveryEnabled(); }

    public void setHost(final String host) { this.factory.setHost(host); }

    public void setPort(final int port) { this.factory.setPort(port); }

    public void setUserName(final String userName) { this.factory.setUsername(userName); }

    public void setPassword(final String password) { this.factory.setPassword(password); }

    public void setAutomaticRecovery(final Boolean automaticRecovery) { this.factory.setAutomaticRecoveryEnabled(automaticRecovery); }
}
