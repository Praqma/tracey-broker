package net.praqma.tracey.broker.rabbitmq;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 *
 * @author Mads
 */
public class TraceyPayloadFilterTest {

    static PayloadRegexFilter filter = new PayloadRegexFilter(".*keyword.*");

    @Test
    public void reject() {
        String response = filter.postReceive("reject");
        assertNull(response);
    }

    @Test
    public void accept() {
        String response = filter.postReceive("there was a keyword found here.");
        assertNotNull(response);
    }
}
