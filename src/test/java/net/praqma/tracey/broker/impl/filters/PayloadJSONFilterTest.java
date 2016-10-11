package net.praqma.tracey.broker.impl.filters;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

/**
 *
 */
public class PayloadJSONFilterTest {

    private PayloadJSONFilter filterMatch = new PayloadJSONFilter("$..*[?(@.id == 2)]");
    private PayloadJSONFilter filterNotMatch = new PayloadJSONFilter("$..*[?(@.id == 3)]");

    private PayloadJSONFilter match = new PayloadJSONFilter("Name", "Student1");
    private PayloadJSONFilter notMatch = new PayloadJSONFilter("id", "one");

    public PayloadJSONFilterTest() throws IOException {
    }

    private String getFileContent(final String fileName) throws IOException {
        final String path = PayloadJSONFilterTest.class.getResource(fileName).getPath();
        final File f = new File(path);
        return FileUtils.readFileToString(f, "UTF-8");
    }
    @Test
    public void acceptKeyValue() throws Exception {
        final String payload = match.postReceive(getFileContent("message.json"));
        Assert.assertNotNull(payload);
    }

    @Test
    public void rejectKeyValue() throws Exception {
        final String payload = notMatch.postReceive(getFileContent("message.json"));
        Assert.assertNull(payload);
    }
    @Test
    public void acceptPatternJsonReg() throws Exception {
        final String payload = filterMatch.postReceive(getFileContent("message.json"));
        Assert.assertNotNull(payload);
    }

    @Test
    public void rejectPatternJsonReg() throws Exception {
        final String payload = filterNotMatch.postReceive(getFileContent("message.json"));
        Assert.assertNull(payload);
    }

    @Test
    public void wrongJson() throws Exception {
        final String payload = filterMatch.postReceive(getFileContent("wrong.json"));
        Assert.assertNull(payload);
    }

    @Test
    public void test() throws Exception {
        final PayloadJSONFilter filter = new PayloadJSONFilter("..[?(@.id == 2)]");
        final String payload = filter.postReceive(getFileContent("message.json"));
        Assert.assertNull(payload);
    }
}