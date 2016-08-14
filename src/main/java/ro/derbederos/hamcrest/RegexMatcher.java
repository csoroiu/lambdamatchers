package ro.derbederos.hamcrest;

import org.hamcrest.Matcher;
import org.hamcrest.core.SubstringMatcher;

import java.util.regex.Pattern;

public class RegexMatcher extends SubstringMatcher {

    private final Pattern pattern;
    private final boolean fullMatch;

    private RegexMatcher(String regex, boolean fullMatch) {
        this(Pattern.compile(regex), fullMatch);
    }

    private RegexMatcher(Pattern pattern, boolean fullMatch) {
        super(fullMatch ? "matching pattern" : "containing pattern", false, pattern.pattern());
        this.pattern = pattern;
        this.fullMatch = fullMatch;
    }

    @Override
    protected boolean evalSubstringOf(String actual) {
        if (fullMatch) {
            return pattern.matcher(actual).matches();
        } else {
            return pattern.matcher(actual).find();
        }
    }

    public static Matcher<String> matchesPattern(String regex) {
        return new RegexMatcher(regex, true);
    }

    public static Matcher<String> matchesPattern(String regex, int patternFlags) {
        return new RegexMatcher(Pattern.compile(regex, patternFlags), true);
    }

    public static Matcher<String> matchesPattern(Pattern pattern) {
        return new RegexMatcher(pattern, true);
    }

    public static Matcher<String> containsPattern(String regex) {
        return new RegexMatcher(regex, false);
    }

    public static Matcher<String> containsPattern(String regex, int patternFlags) {
        return new RegexMatcher(Pattern.compile(regex, patternFlags), false);
    }

    public static Matcher<String> containsPattern(Pattern pattern) {
        return new RegexMatcher(pattern, false);
    }
//    assertThat("Hello, world!", containsPattern("[wW]orld"));
//    assertThat("Hello, world!", matchesPattern("Hello, .+!"));
}
