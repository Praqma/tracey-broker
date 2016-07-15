package net.praqma.tracey.broker.rabbitmq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mads
 */
public class TraceyEventTypeFilter implements TraceyFilter {

    private Set<String> events = new HashSet<>();

    public TraceyEventTypeFilter accept(String... classNames) {
        events.addAll(Arrays.asList(classNames));
        return this;
    }


    public List<String> routingKeys() {
        if(events.isEmpty()) {
            return Arrays.asList("tracey.event.#");
        } else {
            List<String> types = new ArrayList<>();
            for(String evt : events) {
                types.add("tracey.event.eiffel."+evt.toLowerCase());
            }
            return types;
        }
    }

    public TraceyEventTypeFilter() { }

    public TraceyEventTypeFilter(String... eventTypes) throws ClassNotFoundException {
        events.addAll(Arrays.asList(eventTypes));
    }

    @Override
    public List<String> preReceive() {
        return routingKeys();
    }

    @Override
    public String postReceive(String payload) {
        return payload;
    }
}
