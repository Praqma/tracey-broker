package net.praqma.tracey.broker.impl.filters;

import org.junit.Assert;

import org.junit.Test;

/**
 *
 * @author Mads
 */
public class TraceyPayloadFilterTest {

    private static final PayloadRegexFilter filter = new PayloadRegexFilter(".*keyword.*");

    @Test
    public void reject() {
        final String response = filter.postReceive("reject");
        Assert.assertNull(response);
    }

    @Test
    public void accept() {
        final String response = filter.postReceive("there was a keyword found here.");
        Assert.assertNotNull(response);
    }
}
