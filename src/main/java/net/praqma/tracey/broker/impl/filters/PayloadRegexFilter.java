package net.praqma.tracey.broker.impl.filters;

import net.praqma.tracey.broker.api.TraceyFilter;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayloadRegexFilter implements TraceyFilter {

    private String regex = ".*";
    private Pattern regexCompiled;

    public PayloadRegexFilter(final String regex) {
        this.regex = regex;
        regexCompiled = Pattern.compile(this.regex, Pattern.MULTILINE);
    }

    @Override
    public List<String> preReceive() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String postReceive(final String payload) {
        final Matcher m = getRegexCompiled().matcher(payload);
        if(m.matches()) {
            return payload;
        }
        return null;
    }

    /**
     * @return the regex
     */
    public String getRegex() {
        return regex;
    }

    /**
     * @param regex the regex to set
     */
    public void setRegex(final String regex) {
        this.regex = regex;
    }

    /**
     * @return the regexCompiled
     */
    public Pattern getRegexCompiled() {
        return regexCompiled;
    }

    /**
     * @param regexCompiled the regexCompiled to set
     */
    public void setRegexCompiled(final Pattern regexCompiled) {
        this.regexCompiled = regexCompiled;
    }

}
