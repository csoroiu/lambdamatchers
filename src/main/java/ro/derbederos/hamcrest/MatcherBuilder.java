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

import static java8.util.Objects.requireNonNull;

/**
 * Class used to easily create a custom matcher. Inspired from https://gist.github.com/tadams/10680655.
 * <p>
 * Example:
 * <pre>
 * return MatcherBuilder
 *         .of(Person.class)
 *         .matches(expected, TestClass::myCustomEquals)
 *         .description(expected::getName)
 *         .describeMismatch(actual -&gt; "was " + actual.getName())
 *         .build();
 *  </pre>
 *
 * @param <T> The type of the matched object.
 * @since 0.11
 */
public final class MatcherBuilder<T> {
    private final Class<? super T> inputType;
    private Predicate<T> matchesSafely;
    private Consumer<Description> describeTo;
    private BiConsumer<T, Description> describeMismatchSafely;

    private MatcherBuilder(Class<? super T> inputType) {
        this.inputType = inputType;
    }

    /**
     * Creates a {@link MatcherBuilder} for a specified {@code type}.
     * <p>
     *
     * @param type The type of the checked object.
     * @param <T>  The generic type of the checked object.
     * @return The newly created {@link MatcherBuilder}.
     * @since 0.11
     */
    public static <T> MatcherBuilder<T> of(Class<? super T> type) {
        return new MatcherBuilder<>(requireNonNull(type));
    }

    /**
     * Sets a {@link Predicate} that will be used to match the input value.
     * <p>
     * The {@link Predicate} receives as input the value to be checked.
     *
     * @param predicate The predicate.
     * @return {@code this} {@link MatcherBuilder}.
     * @since 0.11
     */
    public MatcherBuilder<T> matches(Predicate<T> predicate) {
        this.matchesSafely = requireNonNull(predicate);
        return this;
    }

    /**
     * Sets a {@link BiPredicate} that will be used to match the input value against the {@code expected} value.
     * <p>
     * The {@link BiPredicate} receives as input the {@code expected} value and the input value.
     *
     * @param expected  The expected value.
     * @param predicate The predicate.
     * @return {@code this} {@link MatcherBuilder}.
     * @since 0.11
     */
    public MatcherBuilder<T> matches(T expected, BiPredicate<T, T> predicate) {
        requireNonNull(predicate);
        this.matchesSafely = (T actual) -> predicate.test(expected, actual);
        return this;
    }

    /**
     * Sets the describer of the matcher which is a {@link Consumer}.
     * <p>
     * The {@link Consumer} receives as input the {@link Description} and can set it.
     *
     * @param describer The describer of the matcher.
     * @return {@code this} {@link MatcherBuilder}.
     * @since 0.11
     */
    public MatcherBuilder<T> description(Consumer<Description> describer) {
        this.describeTo = requireNonNull(describer);
        return this;
    }

    /**
     * Sets a {@link Supplier} which will provide the description of the matcher.
     *
     * @param descriptionSupplier The supplier for the description.
     * @return {@code this} {@link MatcherBuilder}.
     * @since 0.11
     */
    public MatcherBuilder<T> description(Supplier<String> descriptionSupplier) {
        requireNonNull(descriptionSupplier);
        return description(d -> d.appendText(descriptionSupplier.get()));
    }

    /**
     * Sets the description of the matcher.
     *
     * @param description The description of the matcher.
     * @return {@code this} {@link MatcherBuilder}.
     * @since 0.11
     */
    public MatcherBuilder<T> description(String description) {
        requireNonNull(description);
        return description(d -> d.appendText(description));
    }

    /**
     * Sets the mismatchDescriber of the matcher which is a {@link BiConsumer}.
     * <p>
     * The {@link BiConsumer} receives as input the input value and the {@link Description} and can set the description.
     *
     * @param mismatchDescriber The mismatchDescriber of the matcher.
     * @return {@code this} {@link MatcherBuilder}.
     * @since 0.11
     */
    public MatcherBuilder<T> describeMismatch(BiConsumer<T, Description> mismatchDescriber) {
        requireNonNull(mismatchDescriber);
        this.describeMismatchSafely = mismatchDescriber;
        return this;
    }

    /**
     * Sets a {@link Function} which will provide the mismatch description of the matcher.
     * <p>
     * The {@link Function} will receive as input the input value and will have to provide a text.
     *
     * @param mismatchFunction The mismatchFunction for the mismatch description.
     * @return {@code this} {@link MatcherBuilder}.
     * @since 0.11
     */
    public MatcherBuilder<T> describeMismatch(Function<T, String> mismatchFunction) {
        requireNonNull(mismatchFunction);
        return describeMismatch((actual, d) -> d.appendText(mismatchFunction.apply(actual)));
    }

    /**
     * This method will create and return the custom {@link Matcher}.
     *
     * @return The custom {@link Matcher}.
     * @since 0.11
     */
    public Matcher<T> build() {
        requireNonNull(describeTo, "Description was not set");
        return new FuncTypeSafeMatcher<>(inputType, matchesSafely, describeTo, describeMismatchSafely);
    }
}
