package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.AMQP;
import net.praqma.tracey.broker.TraceyBroker;
import net.praqma.tracey.broker.TraceyMessageValidator;
import net.praqma.tracey.broker.TraceyValidatorError;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import groovy.util.ConfigObject;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.praqma.tracey.broker.TraceyIOError;
import net.praqma.tracey.core.*;

public class TraceyRabbitMQBrokerImpl implements TraceyBroker {

    private static final Logger LOG = Logger.getLogger(TraceyRabbitMQBrokerImpl.class.getName());
    private final TraceyMessageValidator validator;
    private ConnectionFactory factory = new ConnectionFactory();
    private TraceyRabbitMQConfiguration config = new TraceyRabbitMQConfiguration();
    private final String VERSION = "1.0-beta2";

    private static class TraceyRabbitMQConfiguration {
        String host = "localhost";
        String exchange = "";
        String queue = "default";
        boolean durable = true;
    }

    /**
     * Parses a config file in this form:
     * broker {
            rabbitmq {
                host = 'localhost'
                durable = true
                exchange = ''
                queue = ''
            }
       }
     *
     * @param f
     */
    @Override
    public void configure(File f)  {
        if(f != null) {
            TraceyDefaultParserImpl parser = new TraceyDefaultParserImpl();
            Map m = ((ConfigObject)parser.parse(f)).flatten();
            String host = (String)m.get("broker.rabbitmq.host");
            Boolean durable = (boolean)m.get("broker.rabbitmq.durable");
            String exchange = (String)m.get("broker.rabbitmq.exchange");
            String queue = (String)m.get("broker.rabbitmq.queue");
            TraceyRabbitMQConfiguration conf = new TraceyRabbitMQConfiguration();
            conf.durable = durable;
            conf.exchange = exchange;
            conf.queue = queue;
            conf.host = host;
            config = conf;
        }
    }

    public TraceyRabbitMQBrokerImpl() {
        this.validator = (String message) -> {
            System.out.println("No validation - Default");
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
            channel.queueDeclare(config.queue, config.durable, false, false, null);
            channel.basicPublish(config.exchange, destination, null, payload.getBytes());
            channel.close();
            connection.close();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Unable to send message with RabbitMQ", ex);
            throw new TraceyRabbitMQError(String.format("Unable to send:%n%s%nDestination: %s", payload, destination), ex);
        }
        return payload;
    }

    @Override
    public String recieve(String source) {
        try {
            factory.setHost(config.host);
            final Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();
            String q = source == null ? config.queue : source;
            channel.queueDeclare(q, config.durable, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            System.out.println(" [*] Version    : "+VERSION);
            System.out.println(" [*] Using queue: "+q);
            System.out.println(" [*] Durable    : "+config.durable);
            System.out.println(" [*] Exchange   : "+config.exchange);
            System.out.println(" [*] Host       : "+config.host);
            channel.basicQos(1);
            final Consumer consumer = new DefaultConsumer(channel) {
              @Override
              public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                try {
                    System.out.println(" [x] Received '" + message + "'");
                } finally {
                  System.out.println(" [x] Done");
                  channel.basicAck(envelope.getDeliveryTag(), false);
                }
              }
            };

            channel.basicConsume(q, false, consumer);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error while recieving", ex);
        }
        return "";
    }

}
