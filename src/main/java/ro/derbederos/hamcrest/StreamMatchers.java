/*
 * Copyright (c) 2016-2021 Claudiu Soroiu
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

import java.util.function.Function;
import java.util.stream.BaseStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static ro.derbederos.hamcrest.LambdaMatchers.hasFeature;

/**
 * <p>
 * This class provides a set of mapping matchers for Java 8+ streams.
 * Basically it contains matchers that convert streams to {@link Iterable} and allow {@link Iterable}
 * matchers to be used.
 * </p>
 * <p>
 * Examples:
 * </p>
 * <pre>
 * assertThat(stream, featureStream(Person::getName, hasItem(startsWith("Ana"))));
 *
 * assertThat(stream, toIterable(hasItem("Ana Pop"));
 *
 * assertThat(Stream.empty(), emptyStream());
 * </pre>
 *
 * @since 0.6
 */
public final class StreamMatchers {

    private StreamMatchers() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * <p>
     * Utility method that creates a matcher that converts a stream of {@code <T>} to an iterable of
     * {@code <U>} allowing us to use an iterable matcher on the result of the mapping function.
     * </p>
     * <p>
     * Example:
     * </p>
     * <pre>
     * assertThat(stream, featureStream(Person::getName, hasItem(startsWith("Ana"))));
     * </pre>
     *
     * @param featureExtractor The function that transforms every element of the input stream.
     * @param iterableMatcher  The matcher to be applied on the resulting iterable.
     * @param <T>              The type of the elements in the input stream.
     * @param <U>              The type of the result of the {@code featureExtractor} function.
     * @since 0.17
     */
    public static <T, U> Matcher<Stream<T>> featureStream(Function<? super T, ? extends U> featureExtractor,
                                                          Matcher<Iterable<? super U>> iterableMatcher) {
        return hasFeature(cacheResultFunction(stream -> streamToIterable(stream.map(featureExtractor))), iterableMatcher);
    }

    /**
     * <p>
     * Creates a {@link Matcher} that applies an iterable {@code matcher} on the input stream. It is an adapter method.
     * </p>
     * <p>
     * Example:
     * </p>
     * <pre>
     * assertThat(stream, toIterable(hasItem("Ana Pop"));
     * </pre>
     *
     * @param matcher The matcher to be applied on the resulting iterable.
     * @param <T>     The type of the elements in the input stream.
     * @since 0.1
     */
    public static <T> Matcher<Stream<T>> toIterable(Matcher<Iterable<? super T>> matcher) {
        return hasFeature(cacheResultFunction(StreamMatchers::streamToIterable), matcher);
    }

    /**
     * <p>
     * Creates a {@link Matcher} that checks if the given {@link BaseStream} is empty.
     * </p>
     * <p>
     * Examples:
     * </p>
     * <pre>
     * assertThat(Stream.empty(), emptyStream());
     * </pre>
     *
     * @param <T> The type of the stream elements.
     * @param <S> The type of the stream implementing {@code BaseStream}.
     * @since 0.1
     */
    public static <T, S extends BaseStream<T, S>> Matcher<BaseStream<T, S>> emptyStream() {
        return hasFeature(cacheResultFunction(StreamMatchers::streamToIterable), emptyIterable());
    }

    private static <T, S extends BaseStream<T, S>> Iterable<T> streamToIterable(BaseStream<T, S> stream) {
        return StreamSupport.stream(stream.spliterator(), false).collect(Collectors.toList());
    }

    private static <E> Matcher<Iterable<? extends E>> emptyIterable() {
        return MatcherBuilder.<Iterable<? extends E>>of(Iterable.class)
                .matches(it -> !it.iterator().hasNext())
                .description("an empty stream")
                .describeMismatch((it, d) -> d.appendText("was ").appendValueList("[", ",", "]", it))
                .build();
    }

    private static <T, R> Function<T, R> cacheResultFunction(Function<? super T, ? extends R> function) {
        return new CacheLastResultFunction<>(function);
    }

    private static class CacheLastResultFunction<T, R> implements Function<T, R> {
        private final Function<? super T, ? extends R> function;
        private boolean initialized = false;
        private T lastInput;
        private R lastValue;

        private CacheLastResultFunction(Function<? super T, ? extends R> function) {
            this.function = function;
        }

        @Override
        public R apply(T t) {
            if (lastInput != t || !initialized) {
                initialized = true;
                lastValue = function.apply(t);
                lastInput = t;
            }
            return lastValue;
        }
    }
}
