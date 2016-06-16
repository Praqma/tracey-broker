package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.praqma.tracey.broker.TraceyIOError;
import net.praqma.tracey.broker.TraceySender;
import net.praqma.tracey.broker.TraceyValidatorError;
import net.praqma.tracey.broker.rabbitmq.TraceyRabbitMQBrokerImpl.ExchangeType;

public class TraceyRabbitMQSenderImpl implements TraceySender {

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
    private ConnectionFactory factory;

    private ExchangeType type = ExchangeType.FANOUT;

    public TraceyRabbitMQSenderImpl() { }

    public TraceyRabbitMQSenderImpl(ConnectionFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public String send(String payload, String destination) throws TraceyValidatorError, TraceyIOError {
        try {
            Connection co = factory.newConnection();
            Channel c = co.createChannel();
            c.exchangeDeclare(destination, ExchangeType.FANOUT.toString());
            c.basicPublish(destination, "", null, payload.getBytes());
            c.close();
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
}
