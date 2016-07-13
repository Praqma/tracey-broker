/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.praqma.tracey.broker.rabbitmq;

import java.util.List;

public interface TraceyFilter {
    //Pre-receive filters create the routing keys that our queues use.
    public List<String> preReceive();

    //Post-recieve hook is called AFTER the message has been routed to the receiver
    //Return null if the payload was rejected, otherwise it returns the payload
    public String postReceive(String payload);
}
