package ro.derbederos.hamcrest;

import org.hamcrest.Matcher;
import org.hamcrest.core.SubstringMatcher;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegexMatcher extends SubstringMatcher {

    private Pattern pattern;

    private RegexMatcher(String regex) {
        this(Pattern.compile(regex));
    }

    private RegexMatcher(Pattern pattern) {
        super(pattern.pattern());
        this.pattern = pattern;
    }

    @Override
    protected boolean evalSubstringOf(String actual) {
        return Objects.equals(substring, actual) || matches(actual);
    }

    private boolean matches(String actual) {
        return pattern.matcher(actual).find();
    }

    @Override
    protected String relationship() {
        return "matching pattern";
    }

    public static Matcher<String> matchesPattern(String regex) {
        return new RegexMatcher(regex);
    }

    public static Matcher<String> matchesPattern(String regex, int patternFlags) {
        return new RegexMatcher(Pattern.compile(regex, patternFlags));
    }

    public static Matcher<String> matchesPattern(Pattern pattern) {
        return new RegexMatcher(pattern);
    }
}
