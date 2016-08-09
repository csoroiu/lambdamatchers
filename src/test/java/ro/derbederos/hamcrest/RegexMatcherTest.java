package ro.derbederos.hamcrest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.regex.Pattern;

import static org.junit.Assert.assertThat;
import static ro.derbederos.hamcrest.RegexMatcher.matchesPattern;

public class RegexMatcherTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testMatchesPatternSubstring() throws Exception {
        assertThat("alabala", matchesPattern("aba"));
    }

    @Test
    public void testMatchesPatternEnding() throws Exception {
        assertThat("alabala", matchesPattern("bala$"));
    }

    @Test
    public void testMatchesPatternStarting() throws Exception {
        assertThat("alabala", matchesPattern("^.la"));
    }

    @Test
    public void testMatchesPatternIgnoreCase1() throws Exception {
        assertThat("alabala", matchesPattern("ALA.ALA", Pattern.CASE_INSENSITIVE));
    }

    @Test
    public void testMatchesPatternIgnoreCase2() throws Exception {
        assertThat("alabala", matchesPattern(Pattern.compile("ALA.ALA", Pattern.CASE_INSENSITIVE)));
    }

    @Test
    public void testMatchesPatternAssertionError() throws Exception {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: a string matching pattern \"ababa\"");
        expectedException.expectMessage("     but: was \"alabala\"");

        assertThat("alabala", matchesPattern("ababa"));
    }
}
