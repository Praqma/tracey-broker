package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import net.praqma.tracey.broker.TraceyBroker;
import net.praqma.tracey.broker.TraceyMessageValidator;
import net.praqma.tracey.broker.TraceyValidatorError;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import groovy.util.ConfigObject;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.praqma.tracey.broker.TraceyIOError;
import net.praqma.tracey.core.*;

public class TraceyRabbitMQBrokerImpl implements TraceyBroker {

    //Logger. We just use standard Java logger
    private static final Logger LOG = Logger.getLogger(TraceyRabbitMQBrokerImpl.class.getName());

    //The attached message validator
    private final TraceyMessageValidator validator;

    private ConnectionFactory factory = new ConnectionFactory();
    private TraceyRabbitMQConfiguration config = new TraceyRabbitMQConfiguration();
    private final String VERSION = "1.0-beta3";
    private Consumer consumer;

    /**
     * @return the handler
     */
    public Consumer getConsumer() {
        return consumer;
    }

    /**
     * @param consumer the handler to set
     */
    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    private static class TraceyRabbitMQConfiguration {
        String host = "localhost";
        String exchange = "tracey";
        String queue = "default";
        boolean durable = true;
    }

    /**
     * Parses a config file in this form: broker { rabbitmq { host = 'localhost'
     * durable = true exchange = '' queue = '' } }
     *
     * @param f
     */
    @Override
    public void configure(File f) {
        if (f != null) {
            TraceyDefaultParserImpl parser = new TraceyDefaultParserImpl();
            Map m = ((ConfigObject) parser.parse(f)).flatten();
            String host = (String) m.get("broker.rabbitmq.host");
            Boolean durable = (boolean) m.get("broker.rabbitmq.durable");
            String exchange = (String) m.get("broker.rabbitmq.exchange");
            String queue = (String) m.get("broker.rabbitmq.queue");
            TraceyRabbitMQConfiguration conf = new TraceyRabbitMQConfiguration();
            conf.durable = durable;
            conf.exchange = exchange;
            conf.queue = queue;
            conf.host = host;
            config = conf;
        }
    }

    public TraceyRabbitMQBrokerImpl() {
        this.validator = new TraceyMessageValidator() {
            @Override
            public void validate(String message) throws TraceyValidatorError {
                //NO-OP
            }
        };
    }

    public TraceyRabbitMQBrokerImpl(TraceyMessageValidator validator) {
        this.validator = validator;
    }

    @Override
    public String send(String payload, String destination) throws TraceyValidatorError, TraceyIOError {
        factory.setHost(config.host);
        validator.validate(payload);
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(destination, "fanout");
            channel.basicPublish(destination, "", null, payload.getBytes());
            channel.close();
            connection.close();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Unable to send message with RabbitMQ", ex);
            throw new TraceyRabbitMQError(String.format("Unable to send:%n%s%nDestination: %s", payload, destination), ex);
        }
        return payload;
    }

    public Channel setUpChannel(String source) throws IOException, TimeoutException {
        factory.setHost(config.host);
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        return channel;
    }

    public String recieve(String source, Consumer consumer) {
        try {
            Channel channel = setUpChannel(source);
            String exchange = source != null ? source : config.exchange;
            channel.exchangeDeclare(exchange, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchange, "");

            System.out.println(" [tracey] Waiting for messages. To exit press CTRL+C");
            System.out.println(" [tracey] Version    : " + VERSION);
            System.out.println(" [tracey] Using queue: " + queueName);
            System.out.println(" [tracey] Exchange   : " + exchange);
            System.out.println(" [tracey] Host       : " + config.host);
            channel.basicConsume(queueName, true, consumer);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error while recieving", ex);
        }

        return "";
    }

    @Override
    public String receive(String source) {
        try {
            factory.setHost(config.host);
            final Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();
            String exchange = source != null ? source : config.exchange;
            channel.exchangeDeclare(exchange, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchange, "");

            System.out.println(" [tracey] Waiting for messages. To exit press CTRL+C");
            System.out.println(" [tracey] Version    : " + VERSION);
            System.out.println(" [tracey] Using queue: " + queueName);
            System.out.println(" [tracey] Exchange   : " + exchange);
            System.out.println(" [tracey] Host       : " + config.host);

            Consumer c = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("[tracey] " + new String(body, "UTF-8"));
                }
            };
            setConsumer(c);

            channel.basicConsume(queueName, false, consumer);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error while recieving", ex);
        }
        return "";
    }

}
