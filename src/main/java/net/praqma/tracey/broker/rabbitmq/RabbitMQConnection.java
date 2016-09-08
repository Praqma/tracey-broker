package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.*;
import groovy.util.ConfigObject;
import net.praqma.tracey.core.TraceyDefaultParserImpl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * Wrapper around RabbitMQ connection class
 */
public class RabbitMQConnection {
    private static final Logger LOG = Logger.getLogger(RabbitMQConnection.class.getName());
    private final ConnectionFactory factory = new ConnectionFactory();
    private Channel channel;

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
     * Declare exchange to communicate with. Will check that exchange doesn't exist on the server and will attempt to
     * declare it. Otherwise will do nothing.
     *
     * @throws IOException when connection error occurs
     */
    public void declareExchange(final String exchangeName, final RabbitMQExchangeType exchangeType, final boolean durable, final boolean autoDelete) throws IOException {
        try {
            LOG.fine("Check that exchange " + exchangeName + " exists");
            channel.exchangeDeclarePassive(exchangeName);
        } catch (IOException e) {
            // the server will raise a 404 channel exception if the named exchange does not exist so we create it
            LOG.fine("Exchange " + exchangeName + " does not exist." +
                    "Create exchange: " + exchangeName +
                    " type: " + exchangeType.toString() +
                    " durable: " + durable +
                    " autoDelete: " + autoDelete);
            channel.exchangeDeclare(exchangeName, exchangeType.toString(), durable, autoDelete, null);
        }
    }

    public static RabbitMQConnection buildFromConfigFile(File f) {
        final TraceyDefaultParserImpl parser = new TraceyDefaultParserImpl();
        final Map m = ((ConfigObject) parser.parse(f)).flatten();
        final String host = (String) getValueOrDefault("broker.rabbitmq.connection.host", m, RabbitMQDefaults.HOST);
        final int port = (int) getValueOrDefault("broker.rabbitmq.connection.port", m, RabbitMQDefaults.PORT);
        final String userName = (String) getValueOrDefault("broker.rabbitmq.connection.userName", m, RabbitMQDefaults.USERNAME);
        final String password = (String) getValueOrDefault("broker.rabbitmq.connection.password", m, RabbitMQDefaults.PASSWORD);
        final boolean automaticRecovery = (Boolean) getValueOrDefault("broker.rabbitmq.connection.automaticRecovery", m, RabbitMQDefaults.AUTOMATIC_RECOVERY);
        return new RabbitMQConnection(host , port, userName, password, automaticRecovery);
    }

    // TODO: move to tracey core TraceyDefaultParserImpl
    private static Object getValueOrDefault(final String key, final Map<String, Object> map, final Object defaultValue) {
        return Optional.ofNullable(map.get(key)).orElse(defaultValue);
    }

    public Channel getChannel() { return channel; }

    public String getHost() { return factory.getHost(); }

    public int getPort() { return factory.getPort(); }

    public String getUserName() { return factory.getUsername(); }

    public String getPassword() { return factory.getPassword(); }

    public Boolean isAutomaticRecoveryEnabled() { return factory.isAutomaticRecoveryEnabled(); }
}
