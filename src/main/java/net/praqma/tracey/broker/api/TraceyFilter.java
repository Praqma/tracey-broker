package net.praqma.tracey.broker.api;

import java.util.List;

public interface TraceyFilter {
    //Pre-receive filters create the routing keys that our queues use.
    List<String> preReceive();

    //Post-recieve hook is called AFTER the message has been routed to the receiver
    //Return null if the payload was rejected, otherwise it returns the payload
    String postReceive(String payload);
}
