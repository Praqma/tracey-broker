package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.praqma.tracey.broker.TraceyIOError;
import net.praqma.tracey.broker.TraceySender;
import net.praqma.tracey.broker.rabbitmq.TraceyRabbitMQBrokerImpl.ExchangeType;

/**
 * <h2>Tracey RabbitMQ sender</h2>
 * <p>
 * Basic implementation. Very simple.
 * </p>
 */
public class TraceyRabbitMQSenderImpl implements TraceySender<RabbitMQRoutingInfo> {

    public ExchangeType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ExchangeType type) {
        this.type = type;
    }

    private static final Logger LOG = Logger.getLogger(TraceyRabbitMQSenderImpl.class.getName());
    private ConnectionFactory factory = new ConnectionFactory();
    private String host = "localhost";
    private int port = 5672;
    private String username;
    private String pw;

    private ExchangeType type = ExchangeType.FANOUT;

    public TraceyRabbitMQSenderImpl() { }

    public TraceyRabbitMQSenderImpl(String host, String username, String password, int port) {
        this.host = host;
        this.username = username;
        this.pw = password;
        this.port = port;
    }

    public final void configure() {
        if(pw != null && !pw.trim().isEmpty()) {
            factory.setPassword(getPw());
        }

        if(username != null)
            factory.setUsername(getUsername());

        factory.setPort(getPort());
        factory.setHost(getHost());
    }

    @Override
    public String send(String payload, RabbitMQRoutingInfo data) throws TraceyIOError {
        try {
            configure();
            TraceyMessageDispatcher d = new TraceyEiffelMessageDispatcher();
            Connection co = factory.newConnection();
            Channel c = co.createChannel();
            d.dispatch(c, data, payload.getBytes(Charset.forName("UTF-8")));
            co.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "IOException", ex);
            throw new TraceyIOError("TraceyIOError", ex);
        } catch (TimeoutException ex) {
            LOG.log(Level.SEVERE, "Timeout occured", ex);
            throw new TraceyIOError("Tracey send timed out", ex);
        }
        return payload;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the pw
     */
    public String getPw() {
        return pw;
    }

    /**
     * @param pw the pw to set
     */
    public void setPw(String pw) {
        this.pw = pw;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the factory
     */
    public ConnectionFactory getFactory() {
        return factory;
    }

    /**
     * @param factory the factory to set
     */
    public void setFactory(ConnectionFactory factory) {
        this.factory = factory;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return String.format("%s@%s:%s", factory.getUsername(), factory.getHost(), factory.getPort());
    }

}
