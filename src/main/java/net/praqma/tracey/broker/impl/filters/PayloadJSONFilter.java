package net.praqma.tracey.broker.impl.filters;

import com.jayway.jsonpath.JsonPath;
import net.praqma.tracey.broker.api.TraceyFilter;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class PayloadJSONFilter implements TraceyFilter {

    private String pattern = "";

    public PayloadJSONFilter(String pattern) {
        this.pattern = pattern;

    }

    @Override
    public List<String> preReceive() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String postReceive(String payload) {
        List<String> read = JsonPath.read(payload, pattern);
        if (!read.isEmpty()){
            return payload;
        }
        return null;
    }
}
