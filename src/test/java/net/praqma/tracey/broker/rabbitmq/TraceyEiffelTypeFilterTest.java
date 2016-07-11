/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.praqma.tracey.broker.rabbitmq;

import com.rabbitmq.client.Channel;
import java.util.List;
import net.praqma.tracey.protocol.eiffel.events.EiffelSourceChangeCreatedEventOuterClass;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author Mads
 */
public class TraceyEiffelTypeFilterTest {

    @Test
    public void testThatAcceptAllIsWorking() {
        Channel c = Mockito.mock(Channel.class);
        TraceyEventTypeFilter filter = new TraceyEventTypeFilter(c, "tracey");
        filter.accept(EiffelSourceChangeCreatedEventOuterClass.EiffelSourceChangeCreatedEvent.class);
        List<String> classes = filter.routingKeys();
        System.out.println(classes.get(0));
        assertEquals("tracey.event.eiffel.eiffelsourcechangecreatedevent", classes.get(0));
    }
}
