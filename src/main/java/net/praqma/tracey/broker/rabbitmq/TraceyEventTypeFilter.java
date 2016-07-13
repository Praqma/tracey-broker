/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.praqma.tracey.broker.rabbitmq;

import com.google.protobuf.GeneratedMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeCreatedEventOuterClass;

/**
 *
 * @author Mads
 */
public class TraceyEventTypeFilter implements TraceyFilter {

    private boolean acceptAll = false;
    private Set<Class<? extends GeneratedMessage>> events = new HashSet<>();

    public TraceyEventTypeFilter accept(Class<? extends GeneratedMessage> event) {
        events.add(event);
        return this;
    }

    public TraceyEventTypeFilter accept(String... className) throws ClassNotFoundException {
        for(String clazz : className) {
            events.add((Class<? extends GeneratedMessage>)Class.forName(clazz));
        }
        return this;
    }


    public List<String> routingKeys() {
        if(events.isEmpty() || isAcceptAll()) {
            return Arrays.asList("tracey.event.#");
        } else {
            ArrayList<String> types =  new ArrayList<>();
            for(Class<? extends GeneratedMessage> evt : events) {
                types.add("tracey.event.eiffel."+evt.getSimpleName().toLowerCase());
            }
            return types;
        }
    }

    public TraceyEventTypeFilter() { }

    public TraceyEventTypeFilter(Class<? extends GeneratedMessage>... evts) {
        events.addAll(Arrays.asList(evts));
    }

    public TraceyEventTypeFilter(String... eventTypes) throws ClassNotFoundException {
        if(eventTypes != null) {
            for(String clazz : eventTypes) {
                events.add((Class<? extends GeneratedMessage>)Class.forName(clazz));
            }
        }
    }

    public static String getClassNameForEiffelSourceChangeCreatedEvent() {
        return EiffelSourceChangeCreatedEventOuterClass.EiffelSourceChangeCreatedEvent.class.getName();
    }

    /**
     * @return the useAll
     */
    public boolean isAcceptAll() {
        return acceptAll;
    }

    /**
     * @param acceptAll
     */
    public void setAcceptAll(boolean acceptAll) {
        this.acceptAll = acceptAll;
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
