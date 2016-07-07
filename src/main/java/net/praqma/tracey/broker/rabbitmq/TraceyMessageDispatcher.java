/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Mads
 */
public interface TraceyMessageDispatcher {
    public void dispatch(Channel channel, String destination, byte[] payload) throws IOException, TimeoutException;
}
