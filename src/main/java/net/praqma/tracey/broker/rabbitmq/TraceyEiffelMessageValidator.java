package net.praqma.tracey.broker.rabbitmq;

import com.google.protobuf.GeneratedMessage;
import org.json.JSONObject;

public class TraceyEiffelMessageValidator {

    public static boolean isA(Class<? extends GeneratedMessage> ext, String payload) {
        JSONObject obj = new JSONObject(payload);
        return obj.getJSONObject("meta") != null && obj.getJSONObject("meta").getString("type").equalsIgnoreCase(ext.getSimpleName().toLowerCase());
    }

    public static boolean isEiffel(String payload) {
        JSONObject obj = new JSONObject(payload);
        return obj.getJSONObject("meta") != null && obj.getJSONObject("meta").getString("type") != null;
    }
}
