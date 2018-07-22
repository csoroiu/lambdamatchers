/*
 * Copyright (c) 2016-2018 Claudiu Soroiu
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

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;

/**
 * Set regular expression matchers for strings.
 *
 * @since 0.1
 */
public final class RegexMatchers {

    private RegexMatchers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Creates a {@link Matcher} that checks if the input char sequence matches the given regular expression {@code pattern}.
     *
     * @param pattern The {@link Pattern} object to match against.
     * @param <T>     The type of the char sequence implementing {@link CharSequence}.
     * @since 0.1
     */
    public static <T extends CharSequence> Matcher<T> matchesPattern(Pattern pattern) {
        return MatcherBuilder.<T>of(CharSequence.class)
                .matches(actual -> pattern.matcher(actual).matches())
                .describeMismatch((item, description) -> description.appendText("was ").appendValue(item))
                .description(description -> description.appendText("a string matching pattern ")
                        .appendValue(pattern.pattern()))
                .build();
    }

    /**
     * Creates a {@link Matcher} that checks if the input char sequence matches the given {@code regex} pattern.
     *
     * @param regex The regular expression to match against.
     * @param <T>   The type of the char sequence implementing {@link CharSequence}.
     * @since 0.1
     */
    public static <T extends CharSequence> Matcher<T> matchesPattern(String regex) {
        return matchesPattern(Pattern.compile(regex));
    }

    /**
     * Creates a {@link Matcher} that checks if the input char sequence matches the given {@code regex} pattern.
     *
     * @param regex        The regular expression to match against.
     * @param patternFlags Match flags, a bit mask that may include
     *                     {@link Pattern#CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL},
     *                     {@link Pattern#UNICODE_CASE}, {@link Pattern#CANON_EQ}, {@link Pattern#UNIX_LINES},
     *                     {@link Pattern#LITERAL}, {@link Pattern#UNICODE_CHARACTER_CLASS}
     *                     and {@link Pattern#COMMENTS}
     * @param <T>          The type of the char sequence implementing {@link CharSequence}.
     * @since 0.1
     */
    public static <T extends CharSequence> Matcher<T> matchesPattern(String regex, int patternFlags) {
        return matchesPattern(Pattern.compile(regex, patternFlags));
    }

    /**
     * Creates a {@link Matcher} that checks if the input char sequence matches any of the given patterns.
     *
     * @param patterns The regular expressions to match against.
     * @since 0.1
     */
    public static Matcher<CharSequence> matchesAnyPattern(String... patterns) {
        ArrayList<Matcher<? super CharSequence>> matchers = new ArrayList<>(patterns.length);
        for (String pattern : patterns) {
            matchers.add(matchesPattern(pattern));
        }
        return anyOf(matchers);
    }

    /**
     * Creates a {@link Matcher} that checks if the input char sequence contains the given regular expression {@code pattern}.
     *
     * @param pattern The {@link Pattern} object to match against.
     * @param <T>     The type of the char sequence implementing {@link CharSequence}.
     * @since 0.1
     */
    public static <T extends CharSequence> Matcher<T> containsPattern(Pattern pattern) {
        return MatcherBuilder.<T>of(CharSequence.class)
                .matches(actual -> pattern.matcher(actual).find())
                .describeMismatch((item, description) -> description.appendText("was ").appendValue(item))
                .description(description -> description.appendText("a string containing pattern ")
                        .appendValue(pattern.pattern()))
                .build();
    }

    /**
     * Creates a {@link Matcher} that checks if the input char sequence <b>contains</b> the given {@code regex} pattern.
     *
     * @param regex The regular expression to match against.
     * @param <T>   The type of the char sequence implementing {@link CharSequence}.
     * @since 0.1
     */
    public static <T extends CharSequence> Matcher<T> containsPattern(String regex) {
        return containsPattern(Pattern.compile(regex));
    }

    /**
     * Creates a {@link Matcher} that checks if the input char sequence <b>contains</b> the given {@code regex} pattern.
     *
     * @param regex        The regular expression to match against.
     * @param patternFlags Match flags, a bit mask that may include
     *                     {@link Pattern#CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL},
     *                     {@link Pattern#UNICODE_CASE}, {@link Pattern#CANON_EQ}, {@link Pattern#UNIX_LINES},
     *                     {@link Pattern#LITERAL}, {@link Pattern#UNICODE_CHARACTER_CLASS}
     *                     and {@link Pattern#COMMENTS}
     * @param <T>          The type of the char sequence implementing {@link CharSequence}.
     * @since 0.1
     */
    public static <T extends CharSequence> Matcher<T> containsPattern(String regex, int patternFlags) {
        return containsPattern(Pattern.compile(regex, patternFlags));
    }

    /**
     * Creates a {@link Matcher} that checks if the input char sequence <b>contains</b> any of the given patterns.
     *
     * @param patterns The regular expressions to match against.
     * @since 0.1
     */
    public static Matcher<CharSequence> containsAnyPattern(String... patterns) {
        ArrayList<Matcher<? super CharSequence>> matchers = new ArrayList<>(patterns.length);
        for (String pattern : patterns) {
            matchers.add(containsPattern(pattern));
        }
        return anyOf(matchers);
    }

    /**
     * Creates a {@link Matcher} that checks if the input char sequence <b>contains</b> all of the given patterns.
     *
     * @param patterns The regular expressions to match against.
     * @since 0.1
     */
    public static Matcher<CharSequence> containsAllPatterns(String... patterns) {
        ArrayList<Matcher<? super CharSequence>> matchers = new ArrayList<>(patterns.length);
        for (String pattern : patterns) {
            matchers.add(containsPattern(pattern));
        }
        return allOf(matchers);
    }
}
