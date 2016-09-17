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

import java.util.ArrayList;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;

/**
 * This class provides a set of regular expressions matchers for strings.
 *
 * @since 0.1
 */
public final class RegexMatchers {

    private RegexMatchers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Checks if the input string <b>matches</b> the given regular expression <code>pattern</code>.
     *
     * @param pattern The {@link Pattern} object to check against.
     * @since 0.1
     */
    public static Matcher<String> matchesPattern(Pattern pattern) {
        return PatternMatchesMatcher.matchesPattern(pattern);
    }

    /**
     * Checks if the input string <b>matches</b> the given <code>regex</code> pattern.
     *
     * @param regex The regular expression to check against.
     * @since 0.1
     */
    public static Matcher<String> matchesPattern(String regex) {
        return matchesPattern(Pattern.compile(regex));
    }

    /**
     * Checks if the input string <b>matches</b> the given <code>regex</code> pattern.
     *
     * @param regex        The regular expression to check against.
     * @param patternFlags Match flags, a bit mask that may include
     *                     {@link Pattern#CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL},
     *                     {@link Pattern#UNICODE_CASE}, {@link Pattern#CANON_EQ}, {@link Pattern#UNIX_LINES},
     *                     {@link Pattern#LITERAL}, {@link Pattern#UNICODE_CHARACTER_CLASS}
     *                     and {@link Pattern#COMMENTS}
     * @since 0.1
     */
    public static Matcher<String> matchesPattern(String regex, int patternFlags) {
        return matchesPattern(Pattern.compile(regex, patternFlags));
    }

    /**
     * Checks if the input string <b>matches any</b> of the given patterns.
     *
     * @param patterns The regular expressions to check against.
     * @since 0.1
     */
    public static Matcher<? super String> matchesAnyPattern(String... patterns) {
        ArrayList<Matcher<? super String>> matchers = new ArrayList<>(patterns.length);
        for (String pattern : patterns) {
            matchers.add(matchesPattern(pattern));
        }
        return anyOf(matchers);
    }

    /**
     * Checks if the input string <b>contains</b> the given regular expression <code>pattern</code>.
     *
     * @param pattern The {@link Pattern} object to check against.
     * @since 0.1
     */
    public static Matcher<String> containsPattern(Pattern pattern) {
        return PatternFindMatcher.containsPattern(pattern);
    }

    /**
     * Checks if the input string <b>contains</b> the given <code>regex</code> pattern.
     *
     * @param regex The regular expression to check against.
     * @since 0.1
     */
    public static Matcher<String> containsPattern(String regex) {
        return containsPattern(Pattern.compile(regex));
    }

    /**
     * Checks if the input string <b>contains</b> the given <code>regex</code> pattern.
     *
     * @param regex        The regular expression to check against.
     * @param patternFlags Match flags, a bit mask that may include
     *                     {@link Pattern#CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL},
     *                     {@link Pattern#UNICODE_CASE}, {@link Pattern#CANON_EQ}, {@link Pattern#UNIX_LINES},
     *                     {@link Pattern#LITERAL}, {@link Pattern#UNICODE_CHARACTER_CLASS}
     *                     and {@link Pattern#COMMENTS}
     * @since 0.1
     */
    public static Matcher<String> containsPattern(String regex, int patternFlags) {
        return containsPattern(Pattern.compile(regex, patternFlags));
    }

    /**
     * Checks if the input string <b>contains any</b> of the given patterns.
     *
     * @param patterns The regular expressions to check against.
     * @since 0.1
     */
    public static Matcher<? super String> containsAnyPattern(String... patterns) {
        ArrayList<Matcher<? super String>> matchers = new ArrayList<>(patterns.length);
        for (String pattern : patterns) {
            matchers.add(containsPattern(pattern));
        }
        return anyOf(matchers);
    }

    /**
     * Checks if the input string <b>contains all</b> of the given patterns.
     *
     * @param patterns The regular expressions to check against.
     * @since 0.1
     */
    public static Matcher<? super String> containsAllPatterns(String... patterns) {
        ArrayList<Matcher<? super String>> matchers = new ArrayList<>(patterns.length);
        for (String pattern : patterns) {
            matchers.add(containsPattern(pattern));
        }
        return allOf(matchers);
    }
}
