package net.praqma.tracey.broker.impl.filters;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import net.praqma.tracey.broker.api.TraceyFilter;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class PayloadJSONFilter implements TraceyFilter {

    private String pattern = "";
    private static final Logger LOG = Logger.getLogger(PayloadJSONFilter.class.getName());


    public PayloadJSONFilter(String pattern) {
        this.pattern = pattern;

    }

    public PayloadJSONFilter(String key, String value){
        this.pattern = String.format("$..*[?(@.%s == \"%s\")]", key, value);
    }

    @Override
    public List<String> preReceive() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String postReceive(String payload) throws JsonPathException {
        try {
            List<String> read = JsonPath.read(payload, pattern);
            if (!read.isEmpty()) {
                return payload;
            }
        }
        catch(JsonPathException e) {
            LOG.log(Level.ALL, "Invalid format of json or pattern", e);
        }
        return null;
    }
}
