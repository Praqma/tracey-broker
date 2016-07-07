package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
/*
import net.praqma.tracey.protocol.eiffel.EiffelEventFactory;
import net.praqma.tracey.protocol.eiffel.EiffelEventOuterClass.*;
import net.praqma.tracey.protocol.eiffel.EiffelSourceChangeCreatedEventFactory;
import net.praqma.tracey.protocol.eiffel.EiffelSourceChangeCreatedEventOuterClass.*;
import net.praqma.tracey.protocol.eiffel.MetaFactory;
*/
/**
 *
 * @author Mads
 */
public class TraceyEiffelMessageDispatcher implements TraceyMessageDispatcher {
    @Override
    public void dispatch(Channel c, String destination, byte[] payload) throws IOException, TimeoutException {
        c.exchangeDeclare(destination, TraceyRabbitMQBrokerImpl.ExchangeType.TOPIC.toString());
        c.basicPublish(destination, createRoutingKey(payload), null, payload);
        c.close();
    }

    public String createRoutingKey(byte[] payload) {
        String d = "tracey.event.default";
        /*
        try {
            EiffelEvent evt = EiffelEvent.parseFrom(payload);
            d = "tracey.event.eiffel."+evt.getClass().getSimpleName().toLower();
        } catch (InvalidProtocolBufferException error) {

        }
*/



        return d;
    }
}
