package net.praqma.tracey.broker.rabbitmq;

import com.google.protobuf.GeneratedMessage;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeCreatedEventOuterClass;
import org.json.JSONObject;

//TODO: This should be more Object oriented
public class TraceyEiffelMessageValidator {

    public static boolean isA(Class<? extends GeneratedMessage> ext, String payload) {
        JSONObject obj = new JSONObject(payload);
        return obj.getJSONObject("meta") != null && obj.getJSONObject("meta").getString("type").equalsIgnoreCase(ext.getSimpleName());
    }

    public static boolean isA(String typename, String payload) {
        JSONObject obj = new JSONObject(payload);
        return obj.getJSONObject("meta") != null && obj.getJSONObject("meta").getString("type").equalsIgnoreCase(typename);
    }

    public static boolean isEiffel(String payload) {
        JSONObject obj = new JSONObject(payload);
        return obj.getJSONObject("meta") != null && obj.getJSONObject("meta").getString("type") != null;
    }

    public static JSONObject getGitIdentifier(String payload) {
        if (!isA(EiffelSourceChangeCreatedEventOuterClass.EiffelSourceChangeCreatedEvent.class, payload)) {
            return null;
        }
        JSONObject obj = new JSONObject(payload).getJSONObject("data").getJSONObject("gitIdentifier");
        return obj;
    }
}
