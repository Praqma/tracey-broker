/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.praqma.tracey.broker.rabbitmq;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Mads
 */
public class TraceyEiffelTypeFilterTest {

    static TraceyEventTypeFilter filter = new TraceyEventTypeFilter();

    @BeforeClass
    public static void setup() {
        filter.accept("EiffelSourceChangeCreatedEvent");
    }

    @Test
    public void testThatAcceptAllIsWorking() throws Exception {
        List<String> classes = filter.routingKeys();
        assertEquals("tracey.event.eiffel.eiffelsourcechangecreatedevent", classes.get(0));
    }

    @Test
    public void payloadThatDoesNotGetFiltered() {
        assertEquals("Not eiffel message. Should pass.", filter.postReceive("Not eiffel message. Should pass."));
    }

    @Test
    public void acceptPayloadThatPassThroughFilter() throws IOException, URISyntaxException {
        URI url = DispatcherTest.class.getResource("sourcechangeevent.json").toURI();
        Path p = Paths.get(url);
        byte[] data = Files.readAllBytes(p);
        String output = filter.postReceive(new String(data, "UTF-8"));
        assertNotNull(output);
        assertEquals(new String(data,"UTF-8"), output);
    }

    @Test
    public void acceptAdditonal() throws ClassNotFoundException {
        filter.accept("EiffelFutureEvent");
        List<String> classes = filter.routingKeys();
        assertEquals("tracey.event.eiffel.eiffelfutureevent", classes.get(1));
    }

}
