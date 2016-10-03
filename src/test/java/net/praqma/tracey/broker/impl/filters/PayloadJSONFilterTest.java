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

    private PayloadJSONFilter match = new PayloadJSONFilter("Name", "Student1");
    private PayloadJSONFilter notMatch = new PayloadJSONFilter("id", "one");

    public PayloadJSONFilterTest() throws IOException {
    }

    private String getFileContent(String fileName) throws IOException {
        String path = PayloadJSONFilterTest.class.getResource(fileName).getPath();
        File f = new File(path);
        return FileUtils.readFileToString(f, "UTF-8");
    }
    @Test
    public void acceptKeyValue() throws Exception {
        String payload = match.postReceive(getFileContent("message.json"));
        assertNotNull(payload);
    }

    @Test
    public void rejectKeyValue() throws Exception {
        String payload = notMatch.postReceive(getFileContent("message.json"));
        assertNull(payload);
    }
    @Test
    public void acceptPatternJsonReg() throws Exception {
        String payload = filterMatch.postReceive(getFileContent("message.json"));
        assertNotNull(payload);
    }

    @Test
    public void rejectPatternJsonReg() throws Exception {
        String payload = filterNotMatch.postReceive(getFileContent("message.json"));
        assertNull(payload);
    }

    @Test
    public void wrongJson() throws Exception {
        String payload = filterMatch.postReceive(getFileContent("wrong.json"));
        assertNull(payload);
    }

    @Test
    public void test() throws Exception {
        PayloadJSONFilter filter = new PayloadJSONFilter("..[?(@.id == 2)]");
        String payload = filter.postReceive(getFileContent("message.json"));
        assertNull(payload);
    }
}