package ro.derbederos.hamcrest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.regex.Pattern;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static ro.derbederos.hamcrest.RegexMatcher.*;

public class RegexMatcherTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testMatchesPatternSubstring() throws Exception {
        assertThat("alabala", matchesPattern("alabala"));
    }

    @Test
    public void testContainsPatternSubstring() throws Exception {
        assertThat("alabala", containsPattern("aba"));
    }

    @Test
    public void testMatchesPatternEnding() throws Exception {
        assertThat("alabala", matchesPattern("\\w*bala$"));
    }

    @Test
    public void testContainsPatternEnding() throws Exception {
        assertThat("alabala", containsPattern("bala$"));
    }

    @Test
    public void testMatchesPatternStarting() throws Exception {
        assertThat("alabala", matchesPattern("^.labala"));
    }

    @Test
    public void testContainsPatternStarting() throws Exception {
        assertThat("alabala", containsPattern("^.la"));
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
    public void testContainsPatternIgnoreCase1() throws Exception {
        assertThat("alabala", containsPattern("LA.A", Pattern.CASE_INSENSITIVE));
    }

    @Test
    public void testContainsPatternIgnoreCase2() throws Exception {
        assertThat("alabala", containsPattern(Pattern.compile("LA.A", Pattern.CASE_INSENSITIVE)));
    }

    @Test
    public void testMatchesPatternAssertionError() throws Exception {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: a string matching pattern \"ababa\"");
        expectedException.expectMessage("     but: was \"alabala\"");

        assertThat("alabala", matchesPattern("ababa"));
    }

    @Test
    public void matchesStringToPatterns() {
        assertThat("zxc456", matchesAnyPattern("[zxc]+\\d{3}", "[abc]+"));
    }

    @Test
    public void matchesStringToPattern() {
        assertThat("abc123", allOf(matchesPattern("[a-c]+\\d{3}"), not(matchesPattern("[d-f]+\\d{4}"))));
    }

    @Test
    public void checksIfStringContainsPattern() {
        assertThat("aardvark", allOf(containsPattern("rdva"), not(matchesPattern("foo"))));
    }

    @Test
    public void checksIfStringContainsAnyPattern() {
        assertThat("awrjbvjkb", allOf(containsAnyPattern("aa", "bb", "jbv"), not(containsAnyPattern("cc", "dd"))));
    }

    @Test
    public void checksIfStringContainsAllPatterns() {
        assertThat("asjbclkjbxhui", allOf(containsAllPatterns("asj", "lkj", "jbx"), not(containsAllPatterns("bcl", "ff"))));
    }
}