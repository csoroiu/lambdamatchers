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

public final class RegexMatchers {

    private RegexMatchers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Matcher<String> matchesPattern(Pattern pattern) {
        return PatternMatchesMatcher.matchesPattern(pattern);
    }

    public static Matcher<String> matchesPattern(String regex) {
        return matchesPattern(Pattern.compile(regex));
    }

    public static Matcher<String> matchesPattern(String regex, int patternFlags) {
        return matchesPattern(Pattern.compile(regex, patternFlags));
    }

    public static Matcher<? super String> matchesAnyPattern(String... patterns) {
        ArrayList<Matcher<? super String>> matchers = new ArrayList<>(patterns.length);
        for (String pattern : patterns) {
            matchers.add(matchesPattern(pattern));
        }
        return anyOf(matchers);
    }

    public static Matcher<String> containsPattern(Pattern pattern) {
        return PatternFindMatcher.containsPattern(pattern);
    }

    public static Matcher<String> containsPattern(String regex) {
        return containsPattern(Pattern.compile(regex));
    }

    public static Matcher<String> containsPattern(String regex, int patternFlags) {
        return containsPattern(Pattern.compile(regex, patternFlags));
    }

    public static Matcher<? super String> containsAnyPattern(String... patterns) {
        ArrayList<Matcher<? super String>> matchers = new ArrayList<>(patterns.length);
        for (String pattern : patterns) {
            matchers.add(containsPattern(pattern));
        }
        return anyOf(matchers);
    }

    public static Matcher<? super String> containsAllPatterns(String... patterns) {
        ArrayList<Matcher<? super String>> matchers = new ArrayList<>(patterns.length);
        for (String pattern : patterns) {
            matchers.add(containsPattern(pattern));
        }
        return allOf(matchers);
    }
}
