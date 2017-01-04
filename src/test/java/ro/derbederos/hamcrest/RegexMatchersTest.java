/*
 * Copyright (c) 2016 Claudiu Soroiu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ro.derbederos.hamcrest;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertDescription;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertMismatchDescription;
import static ro.derbederos.hamcrest.RegexMatchers.*;

public class RegexMatchersTest {
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
    public void testMatchesPatternDescription() throws Exception {
        Matcher<String> regexMatcher = matchesPattern("ababa");
        assertDescription(equalTo("a string matching pattern \"ababa\""), regexMatcher);
        assertMismatchDescription(equalTo("was \"alabala\""), "alabala", regexMatcher);
    }

    @Test
    public void testContainsPatternDescription() throws Exception {
        Matcher<String> regexMatcher = containsPattern("ata");
        assertDescription(equalTo("a string containing pattern \"ata\""), regexMatcher);
        assertMismatchDescription(equalTo("was \"alabala\""), "alabala", regexMatcher);
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