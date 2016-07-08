package net.praqma.tracey.broker.rabbitmq;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeCreatedEventOuterClass;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeCreatedEventOuterClass.EiffelSourceChangeCreatedEvent;

/**
 * The job of the message dispatcher is to ensure that a give message ends up
 * in the correct 'queue'. For rabbitmq that means we need to send the message to
 * the correct exchanges for consumption. We need to decide the routing key based on
 * the payload.
 *
 * @author Mads
 */
public class TraceyEiffelMessageDispatcher implements TraceyMessageDispatcher {

    private static final Logger LOG = Logger.getLogger(TraceyEiffelMessageDispatcher.class.getName());

    @Override
    public void dispatch(Channel c, String destination, byte[] payload) throws IOException, TimeoutException {
        c.exchangeDeclare(destination, TraceyRabbitMQBrokerImpl.ExchangeType.TOPIC.toString());
        c.basicPublish(destination, createRoutingKey(payload), null, payload);
        c.close();
    }

    public String createRoutingKey(byte[] payload) {
        String d = "tracey.event.default";
        try {
            EiffelSourceChangeCreatedEvent evt = EiffelSourceChangeCreatedEventOuterClass.EiffelSourceChangeCreatedEvent.parseFrom(payload);
            d = "tracey.event.eiffel."+evt.getClass().getSimpleName().toLowerCase();
            LOG.info(String.format("Created routing key %s for payload:%n%s", d, new String(payload)));
        } catch (InvalidProtocolBufferException error) {
            LOG.log(Level.SEVERE, String.format("Non eiffel message received, using routing key %s", d), error);
        }

        return d;
    }
}
