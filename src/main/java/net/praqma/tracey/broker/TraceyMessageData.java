package net.praqma.tracey.broker;

import java.util.Objects;
import java.util.Map;

/**
 * Class for data transfer
 * Created by alexandrasedova on 07/09/16.
 */
public class TraceyMessageData {

    private Map<String, Object> headers;
    private int deliveryMode;
    private String routingKey;


    public TraceyMessageData(Map<String, Object> headers, int deliveryMode, String routingKey) {
        this.headers = headers;
        this.deliveryMode = deliveryMode;
        this.routingKey = routingKey;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public int getDeliveryMode() {
        return deliveryMode;
    }

    public String getRoutingKey() {
        return routingKey;
    }
}
