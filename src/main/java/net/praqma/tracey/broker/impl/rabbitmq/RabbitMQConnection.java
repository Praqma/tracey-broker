package net.praqma.tracey.broker.impl.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import groovy.util.ConfigObject;
import net.praqma.tracey.broker.api.ChannelFactory;
import net.praqma.tracey.broker.api.ChannelPool;
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

    private static RabbitMQConnection connection;

    private final ChannelPool channelPool;

    private RabbitMQConnection(final String host, final int port, final String userName, final String password, final Boolean automaticRecovery) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setAutomaticRecoveryEnabled(automaticRecovery);
        ChannelFactory channelFactory = new RabbitMQChannelFactory(factory);
        this.channelPool = new RabbitMQChannelPool(channelFactory);
    }

    public static RabbitMQConnection getConnection(){
        return connection;
    }
    // Being sure the connection does not exist!
    public static RabbitMQConnection create(final String host, final int port, final String userName, final String password, final Boolean automaticRecovery) throws IOException, TimeoutException {
        return connection = new RabbitMQConnection(host, port, userName, password, automaticRecovery);
    }


    public ChannelPool getChannelPool() {
        return channelPool;
    }
    /**
     * Read configuration file and create RabbitMQConnection object.
     * If some fields are not present in provided configuration file then default value from RabbitMQDefaults will be used.
     * userName and passoword fields support environment variable expansion, i.e. you can use env variable name instead of
     * actual value. The following formats supported - $SOMETEXT, ${SOMETEXT}, $SOMETEXT$, %SOMETEXT%
     * @param f File object that contains configuration file
     * @return  RabbitMQConnection object
     */
    public static RabbitMQConnection buildFromConfigFile(final File f) throws IOException, TimeoutException{
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
}
