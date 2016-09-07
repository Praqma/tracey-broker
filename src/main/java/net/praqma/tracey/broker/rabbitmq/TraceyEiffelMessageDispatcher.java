package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.praqma.tracey.broker.TraceyMessageData;
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
    public void dispatch(Channel c, String destination, TraceyMessageData data, byte[] payload) throws IOException, TimeoutException {
        c.exchangeDeclare(destination, TraceyRabbitMQBrokerImpl.ExchangeType.TOPIC.toString());
        c.basicPublish(destination, createRoutingKey(payload), new AMQP.BasicProperties.Builder()
                .headers(data.getHeaders())
                .deliveryMode(data.getDeliveryMode())
                .build(),
                payload);
        c.close();
    }

    public String createRoutingKey(byte[] payload) {
        String d = "tracey.event.default";
        try {
            String type = new JSONObject(new String(payload, "utf-8")).getJSONObject("meta").getString("type");
            d = "tracey.event.eiffel."+type.toLowerCase();
            LOG.info(String.format("Created routing key %s for payload:%n%s", d, new String(payload, "utf-8")));
        } catch (UnsupportedEncodingException | JSONException error) {
            LOG.log(Level.INFO, String.format("Non eiffel message received, using routing key %s", d));
        }

        return d;
    }
}
