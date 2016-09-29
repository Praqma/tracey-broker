package net.praqma.tracey.broker.impl.filters;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class PayloadJSONFilterTest {

    private PayloadJSONFilter filterMatch = new PayloadJSONFilter("$..*[?(@.id == 2)]");
    private PayloadJSONFilter filterNotMatch = new PayloadJSONFilter("$..*[?(@.id == 3)]");

    public PayloadJSONFilterTest() throws IOException {
    }

    private String getFileContent(String fileName) throws IOException {
        String path = PayloadJSONFilterTest.class.getResource(fileName).getPath();
        File f = new File(path);
        return FileUtils.readFileToString(f, "UTF-8");
    }

    @Test
    public void accept() throws Exception {
        String payload = filterMatch.postReceive(getFileContent("message.json"));
        assertNotNull(payload);
    }

    @Test
    public void reject() throws Exception {
        String payload = filterNotMatch.postReceive(getFileContent("message.json"));
        assertNull(payload);
    }

}