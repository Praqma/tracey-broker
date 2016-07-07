package net.praqma.tracey.broker.rabbitmq;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.praqma.tracey.protocol.eiffel.EiffelEventOuterClass.EiffelEvent;


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
            EiffelEvent evt = EiffelEvent.parseFrom(payload);
            d = "tracey.event.eiffel."+evt.getClass().getSimpleName().toLowerCase();
        } catch (InvalidProtocolBufferException error) {
            LOG.log(Level.SEVERE, "Non eiffel message recieved", error);
        }

        return d;
    }
}
