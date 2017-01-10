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

import java8.util.function.*;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static ro.derbederos.hamcrest.TypeFinder.resolveInputType;

public final class MatcherBuilder<T> {
    private final Class<?> inputType;
    private final Predicate<T> matchesSafely;
    private Consumer<Description> describeTo;
    private BiConsumer<T, Description> describeMismatchSafely = (actual, d) -> d.appendText("was ").appendValue(actual);

    private MatcherBuilder(Class<?> inputType, Predicate<T> predicate) {
        this.inputType = inputType;
        this.matchesSafely = predicate;
    }

    public static <T> MatcherBuilder<T> create(Predicate<T> predicate) {
        Class<?> inputType = resolveInputType(Predicate.class, predicate.getClass());
        return new MatcherBuilder<>(inputType, predicate);
    }

    public static <T> MatcherBuilder<T> create(T expected, BiPredicate<T, T> predicate) {
        Class<?> inputType = resolveInputType(BiPredicate.class, predicate.getClass());
        return new MatcherBuilder<>(inputType, actual -> predicate.test(expected, actual));
    }

    public MatcherBuilder<T> description(Consumer<Description> describer) {
        this.describeTo = describer;
        return this;
    }

    public MatcherBuilder<T> description(Supplier<String> descriptionSupplier) {
        return description(d -> d.appendText(descriptionSupplier.get()));
    }

    public MatcherBuilder<T> description(String description) {
        return description(() -> description);
    }

    public MatcherBuilder<T> describeMismatch(BiConsumer<T, Description> mismatchDescriber) {
        this.describeMismatchSafely = mismatchDescriber;
        return this;
    }

    public MatcherBuilder<T> describeMismatch(Function<T, String> mismatchFunction) {
        return describeMismatch((actual, d) -> d.appendText(mismatchFunction.apply(actual)));
    }

    public Matcher<T> build() {
        return new FuncTypeSafeMatcher<>(inputType, matchesSafely, describeTo, describeMismatchSafely);
    }
}
