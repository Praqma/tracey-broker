package net.praqma.tracey.broker.rabbitmq;

import org.json.JSONObject;

public class TraceyEiffelMessageValidator {

    public static boolean isA(String typename, String payload) {
        JSONObject obj = new JSONObject(payload);
        return obj.getJSONObject("meta") != null && obj.getJSONObject("meta").getString("type").equalsIgnoreCase(typename);
    }

    public static boolean isEiffel(String payload) {
        JSONObject obj = new JSONObject(payload);
        return obj.getJSONObject("meta") != null && obj.getJSONObject("meta").getString("type") != null;
    }

    public static JSONObject getGitIdentifier(String payload) {
        if (!isA("EiffelSourceChangeCreatedEvent", payload)) {
            return null;
        }
        JSONObject obj = new JSONObject(payload).getJSONObject("data").getJSONObject("gitIdentifier");
        return obj;
    }
}
