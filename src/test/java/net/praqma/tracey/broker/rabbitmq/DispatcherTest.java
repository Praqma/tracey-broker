package net.praqma.tracey.broker.rabbitmq;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DispatcherTest {

    @Test
    public void createDispatcher() throws Exception {
        TraceyEiffelMessageDispatcher dispatcher = new TraceyEiffelMessageDispatcher();
        URI url = DispatcherTest.class.getResource("sourcechangeevent.json").toURI();
        Path p = Paths.get(url);
        byte[] data = Files.readAllBytes(p);
        String key = dispatcher.createRoutingKey(data);
        assertEquals("tracey.event.eiffel.eiffelsourcechangecreatedevent", key);
    }

    @Test
    public void testCreateDefaultErrorRouting() throws Exception {
        TraceyEiffelMessageDispatcher dispatcher = new TraceyEiffelMessageDispatcher();
        URI url = DispatcherTest.class.getResource("sourcechangeevent_broken.json").toURI();
        Path p = Paths.get(url);
        byte[] data = Files.readAllBytes(p);
        String key = dispatcher.createRoutingKey(data);
        assertEquals("tracey.event.default", key);
    }

    @Test
    public void testGitParsing() throws Exception {
        URI url = DispatcherTest.class.getResource("sourcechangeevent.json").toURI();
        Path p = Paths.get(url);
        byte[] data = Files.readAllBytes(p);
        String dataString = new String(data, "utf-8");
        assertTrue(TraceyEiffelMessageValidator.isA("EiffelSourceChangeCreatedEvent", dataString));
        assertTrue(dataString.contains("gitIdentifier"));
        assertNotNull(TraceyEiffelMessageValidator.getGitIdentifier(dataString));
    }
}
