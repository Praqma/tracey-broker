/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.praqma.tracey.broker.rabbitmq;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

/**
 *
 * @author Mads
 */
public class DispatcherTest {

    @Test
    public void createDispatcher() throws Exception {
        TraceyEiffelMessageDispatcher dispatcher = new TraceyEiffelMessageDispatcher();
        URI url = DispatcherTest.class.getResource("sourcechangeevent.json").toURI();
        Path p = Paths.get(url);
        byte[] data = Files.readAllBytes(p);
        String key = dispatcher.createRoutingKey(data);
    }
}
