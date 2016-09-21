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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.regex.Pattern;

final class PatternMatchesMatcher<T extends CharSequence> extends TypeSafeMatcher<T> {

    private final Pattern pattern;

    private PatternMatchesMatcher(Pattern pattern) {
        super(CharSequence.class);
        this.pattern = pattern;
    }

    @Override
    public boolean matchesSafely(T actual) {
        return pattern.matcher(actual).matches();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a string matching pattern ").appendValue(pattern.pattern());
    }

    static <T extends CharSequence> Matcher<T> matchesPattern(Pattern pattern) {
        return new PatternMatchesMatcher<>(pattern);
    }
}
