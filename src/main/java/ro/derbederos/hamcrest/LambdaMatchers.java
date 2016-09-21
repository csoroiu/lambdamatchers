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
import java.util.List;
import java.util.function.Function;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.hamcrest.Matchers.emptyIterable;

/**
 * This class provides a set of mapping matchers based on java 8 functional interfaces (lambdas).
 * <p>
 * They are useful in plenty of places where you are required to make assertions on different properties of objects.
 * <p>
 * It can replace {@link org.hamcrest.Matchers#hasProperty(String, Matcher)} in many places.
 * <p>
 * Also it can be used in places where the property to be read is not a JavaBean compliant property like AtomicXXX.get()
 * methods, non public getters or methods having different names (these are places where the aforementioned matcher
 * cannot be used.
 * <p>
 * The benefit of using the mapping matchers can be seen when dealing with collections of objects.
 * <p>
 * Examples:
 * <pre>
 * assertThat(iterableOfAtomicInteger, everyItem(map(AtomicInteger::get, greaterThanOrEqualTo(21))));
 *
 * assertThat(list, everyItem(map(Person::getAge, greaterThanOrEqualTo(21))));
 *
 * assertThat(list, hasItem(map(Person::getName, startsWith("Alice"))));
 *
 * assertThat(list, mapIterable(Person::getName, hasItem("Ana")));
 *
 * assertThat(array, mapArray(Person::getName, hasItem(startsWith("Ana"))));
 *
 * assertThat(stream, mapStream(Person::getName, hasItem(startsWith("Ana"))));
 * </pre>
 *
 * @since 0.1
 */
public final class LambdaMatchers {

    private LambdaMatchers() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Returns a {@link Matcher} for a feature with the <code>featureDescription</code> description
     * and with the <code>featureName</code> name.
     * The <code>matcher</code> argument will be applied on the result of the <code>mapper</code> function.
     * This method is useful to be used by other utility methods that use functions to convert the input like
     * {@link OptionalMatchers}.
     * <p>
     * <b>This method can be used to create smart {@link org.hamcrest.FeatureMatcher}s.</b>
     *
     * @param mapper             The function that transforms the input.
     * @param featureDescription The description of the <b>feature</b> extracted by the <code>mapper</code>.
     * @param featureName        The name of the <b>feature</b> extracted by the mapper.
     * @param matcher            The {@link Matcher} to be applied on the result of the <code>mapper</code> function.
     * @param <T>                The type of the input.
     * @param <U>                The type of the result of the <code>mapper</code> function.
     * @since 0.1
     */
    public static <T, U> Matcher<T> map(Function<T, U> mapper, String featureDescription, String featureName, Matcher<? super U> matcher) {
        return FunctionMatcher.map(mapper, featureDescription, featureName, matcher);
    }

    /**
     * Utility method to return a functional mapper matcher. It receives as input a <code>mapper</code> and
     * a <code>matcher</code> that will be applied on the result of the <code>mapper</code> function.
     * It tries to auto-magically determine the type of the input object and of the <code>mapper</code> function result.
     * <p>
     * This method is useful ca used to extract properties of objects, or call other functions.
     * It can be used to replace {@link org.hamcrest.Matchers#hasProperty(String, Matcher)}.
     * <p>
     * Examples:
     * <pre>
     * assertThat(iterableOfAtomicInteger, everyItem(map(AtomicInteger::get, greaterThanOrEqualTo(21))));
     *
     * assertThat(list, everyItem(map(Person::getAge, greaterThanOrEqualTo(21))));
     *
     * assertThat(list, hasItem(map(Person::getName, startsWith("Alice"))));
     * </pre>
     *
     * @param mapper  The function that transforms the input.
     * @param matcher The {@link Matcher} to be applied on the result of the <code>mapper</code> function.
     * @param <T>     The type of the input.
     * @param <U>     The type of the result of the <code>mapper</code> function.
     * @since 0.1
     */
    public static <T, U> Matcher<T> map(Function<T, U> mapper, Matcher<? super U> matcher) {
        return FunctionMatcher.map(mapper, matcher);
    }

    /**
     * Utility method that returns a matcher that converts an iterable of <code>&lt;T&gt;</code> to an iterable of
     * <code>&lt;U&gt;</code> allowing us to use an iterable matcher on the result of the mapping function.
     * <p>
     * Example:
     * <pre>
     * assertThat(list, mapIterable(Person::getName, hasItem("Ana")));
     * </pre>
     *
     * @param mapper  The function that transforms every element of the input iterable.
     * @param matcher The matcher to be applied on the resulting iterable.
     * @param <T>     The type of the elements in the input iterable.
     * @param <U>     The type of the result of the <code>mapper</code> function.
     * @since 0.1
     */
    public static <T, U> Matcher<Iterable<? extends T>> mapIterable(Function<T, U> mapper,
            Matcher<Iterable<? super U>> matcher) {
        return map(iter -> (Iterable<? super U>) StreamSupport.stream(iter.spliterator(), false).map(mapper)
                .collect(Collectors.toList()), matcher);
    }

    /**
     * Utility method that returns a matcher that converts an array of <code>&lt;T&gt;</code> to an iterable of
     * <code>&lt;U&gt;</code> allowing us to use an iterable matcher on the result of the mapping function.
     * <p>
     * Example:
     * <pre>
     * assertThat(array, mapArray(Person::getName, hasItem(startsWith("Ana"))));
     * </pre>
     *
     * @param mapper  The function that transforms every element of the input array.
     * @param matcher The matcher to be applied on the resulting iterable.
     * @param <T>     The type of the elements in the input array.
     * @param <U>     The type of the result of the <code>mapper</code> function.
     * @since 0.1
     */
    public static <T, U> Matcher<T[]> mapArray(Function<T, U> mapper, Matcher<Iterable<? super U>> matcher) {
        return map(array -> (Iterable<? super U>) Stream.of(array).map(mapper).collect(Collectors.toList()), matcher);
    }

    /**
     * Utility method that returns a matcher that converts a stream of <code>&lt;T&gt;</code> to an iterable of
     * <code>&lt;U&gt;</code> allowing us to use an iterable matcher on the result of the mapping function.
     * <p>
     * Example:
     * <pre>
     * assertThat(stream, mapStream(Person::getName, hasItem(startsWith("Ana"))));
     * </pre>
     *
     * @param mapper  The function that transforms every element of the input stream.
     * @param matcher The matcher to be applied on the resulting iterable.
     * @param <T>     The type of the elements in the input stream.
     * @param <U>     The type of the result of the <code>mapper</code> function.
     * @since 0.1
     */
    public static <T, U> Matcher<Stream<? extends T>> mapStream(Function<T, U> mapper,
            Matcher<Iterable<? super U>> matcher) {
        return map(stream -> (Iterable<? super U>) stream.map(mapper).collect(Collectors.toList()), matcher);
    }

    /**
     * Returns a {@link Matcher} that applies an iterable <code>matcher</code> on the input stream. It is an adapter method.
     *
     * @param matcher The matcher to be applied on the resulting iterable.
     * @param <T>     The type of the elements in the input stream.
     * @since 0.1
     */
    public static <T> Matcher<Stream<? extends T>> toIterable(Matcher<Iterable<? super T>> matcher) {
        return mapStream(a -> a, matcher);
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link BaseStream} is empty.
     *
     * @param <T> The type of the stream elements.
     * @param <S> The type of the stream implementing {@code BaseStream}.
     * @since 0.1
     */
    public static <T, S extends BaseStream<T, S>> Matcher<BaseStream<T, S>> emptyStream() {
        return map(baseStreamToIterable(), emptyIterable());
    }

    private static <S extends BaseStream<T, S>, T> Function<BaseStream<T, S>, Iterable<T>> baseStreamToIterable() {
        return stream -> {
            List<T> target = new ArrayList<>();
            stream.iterator().forEachRemaining(target::add);
            return target;
        };
    }
}
