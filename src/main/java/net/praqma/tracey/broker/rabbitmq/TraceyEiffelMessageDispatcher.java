package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;

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
            String type = new JSONObject(new String(payload, "utf-8")).getJSONObject("meta").getString("type");
            d = "tracey.event.eiffel."+type.toLowerCase();
            LOG.info(String.format("Created routing key %s for payload:%n%s", d, new String(payload, "utf-8")));
        } catch (Exception error) {
            LOG.log(Level.INFO, String.format("Non eiffel message received, using routing key %s", d));
        }

        return d;
    }

    private String discardInnerClassFromName(String name) {
        if(name.contains(".")) {
            String[] split = name.split(".");
            return split[split.length-1];
        }
        return name;
    }
}
