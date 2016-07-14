/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.praqma.tracey.broker.rabbitmq;

import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Mads
 */
public class TraceyEiffelTypeFilterTest {

    @Test
    public void testThatAcceptAllIsWorking() throws Exception {
        TraceyEventTypeFilter filter = new TraceyEventTypeFilter();
        filter.accept("EiffelSourceChangeCreatedEvent");
        List<String> classes = filter.routingKeys();
        assertEquals("tracey.event.eiffel.eiffelsourcechangecreatedevent", classes.get(0));
    }
}
