/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.praqma.tracey.broker.rabbitmq;

import com.google.protobuf.GeneratedMessage;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.praqma.tracey.broker.rabbitmq.TraceyRabbitMQBrokerImpl.ExchangeType;

/**
 *
 * @author Mads
 */
public class TraceyEventTypeFilter {

    private Channel c;
    private String exchange;
    private boolean acceptAll = false;
    private Set<Class<? extends GeneratedMessage>> events = new HashSet<>();

    public TraceyEventTypeFilter accept(Class<? extends GeneratedMessage> event) {
        events.add(event);
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

    public TraceyEventTypeFilter(Channel c, String exchange) {
        this.c = c;
        this.exchange = exchange;
    }

    /**
     * Applies settings, and returns the name of the created queue.
     * @return The name of the generated queue.
     * @throws java.io.IOException when we fail to bind a queue to the exchange
     */
    public String apply() throws IOException {
        c.exchangeDeclare(exchange, ExchangeType.TOPIC.toString());
        String queueName = c.queueDeclare().getQueue();
        for(String s : routingKeys()) {
            c.queueBind(queueName, exchange, s);
        }
        return queueName;
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


}
