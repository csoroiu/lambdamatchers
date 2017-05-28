/*
 * Copyright (c) 2016-2017 Claudiu Soroiu
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
import org.hamcrest.TypeSafeMatcher;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

// https://gist.github.com/tadams/10680655
final class FuncTypeSafeMatcher<T> extends TypeSafeMatcher<T> {

    private final Predicate<T> matchesSafely;
    private final BiConsumer<T, Description> describeMismatchSafely;
    private final Consumer<Description> describeTo;

    FuncTypeSafeMatcher(Class<? super T> inputType, Predicate<T> matchesSafely,
                        Consumer<Description> describeTo,
                        BiConsumer<T, Description> describeMismatchSafely) {
        super(inputType);
        this.matchesSafely = matchesSafely;
        this.describeTo = describeTo;
        this.describeMismatchSafely = describeMismatchSafely;
    }

    @Override
    protected boolean matchesSafely(T item) {
        return matchesSafely.test(item);
    }

    @Override
    public void describeTo(Description description) {
        describeTo.accept(description);
    }

    @Override
    protected void describeMismatchSafely(T item, Description description) {
        describeMismatchSafely.accept(item, description);
    }
}