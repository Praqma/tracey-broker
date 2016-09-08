package net.praqma.tracey.broker.rabbitmq;

import java.net.URI;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

import net.praqma.tracey.broker.RoutingInfo;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

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

    /**
     * Test dispatcher sends headers
     * @throws Exception
     */
    @Test
    public void testChanellDispatchTraceyMassageData() throws Exception {
        // get payload:
        URI url = DispatcherTest.class.getResource("sourcechangeevent.json").toURI();
        Path p = Paths.get(url);
        byte[] payload = Files.readAllBytes(p);
        // Construct RoutingInfoRabbitMQ and put value to headers
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("key1", "value1");//any or all
        headers.put("Int", 23);
        headers.put("Boolean", true);
        headers.put("List", Arrays.asList("one", "two", "three"));
        // Fill by some data:
        RoutingInfoRabbitMQ data = new RoutingInfoRabbitMQ(headers, "destination", 1, "routingKey");
        TraceyEiffelMessageDispatcher dispatcher = new TraceyEiffelMessageDispatcher();
        Channel c = mock(Channel.class);
        dispatcher.dispatch(c, data, payload);
        //Check if values pass correctly
        ArgumentCaptor<AMQP.BasicProperties> argumentCaptor = ArgumentCaptor.forClass(AMQP.BasicProperties.class);
        //Mockito.verify(c).basicPublish(eq("distenation"), eq(dispatcher.createRoutingKey(payload)), argumentCaptor.capture(),eq(payload));
        Mockito.verify(c).basicPublish(any(String.class), any(String.class), argumentCaptor.capture(),any(byte[].class));
        assertEquals(argumentCaptor.getValue().getHeaders().get("key1"), "value1");
        assertEquals(argumentCaptor.getValue().getHeaders().get("Int"), 23);
        assertEquals(argumentCaptor.getValue().getHeaders().get("Boolean"), true);
        assertTrue(argumentCaptor.getValue().getHeaders().get("List").toString().contains("[one, two, three]"));
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
