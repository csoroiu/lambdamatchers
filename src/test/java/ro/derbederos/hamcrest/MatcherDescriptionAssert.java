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
import org.hamcrest.StringDescription;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

class MatcherDescriptionAssert {
    static void assertDescription(Matcher<String> descriptionMatcher, Matcher<?> matcher) {
        Description description = new StringDescription();
        description.appendDescriptionOf(matcher);
        assertThat("Description does not match.", description.toString(), descriptionMatcher);
    }

    static <T> void assertMismatchDescription(Matcher<String> descriptionMatcher, T arg, Matcher<? super T> matcher) {
        assertThat("Matcher should not match item.", matcher.matches(arg), equalTo(false));
        assertThat("Mismatch description does not match.", mismatchDescription(matcher, arg), descriptionMatcher);
    }

    private static <T> String mismatchDescription(Matcher<? super T> matcher, T arg) {
        Description description = new StringDescription();
        matcher.describeMismatch(arg, description);
        return description.toString().trim();
    }
}
