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

import java8.util.Iterators;
import java8.util.function.Function;
import java8.util.stream.BaseStream;
import java8.util.stream.Collectors;
import java8.util.stream.Stream;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;

import static ro.derbederos.hamcrest.LambdaMatchers.mappedBy;

/**
 * This class provides a set of mapping matchers for streamsupport streams.
 * Basically it contains matchers that convert streams to {@link Iterable} and allow {@link Iterable}
 * matchers to be used.
 * <p>
 * Examples:
 * <pre>
 * assertThat(stream, tmapStream(Person::getName, hasItem(startsWith("Ana"))));
 *
 * assertThat(stream, toIterable(hasItem("Ana Pop"));
 *
 * assertThat(Stream.empty(), emptyStream());
 * </pre>
 *
 * @since 0.11
 */
public final class StreamSupportMatchers {

    private StreamSupportMatchers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Utility method that creates a matcher that converts a stream of {@code <T>} to an iterable of
     * {@code <U>} allowing us to use an iterable matcher on the result of the mapping function.
     * <p>
     * Example:
     * <pre>
     * assertThat(stream, mapStream(Person::getName, hasItem(startsWith("Ana"))));
     * </pre>
     *
     * @param mapper  The function that transforms every element of the input stream.
     * @param matcher The matcher to be applied on the resulting iterable.
     * @param <T>     The type of the elements in the input stream.
     * @param <U>     The type of the result of the {@code mapper} function.
     * @since 0.11
     */
    public static <T, U> Matcher<Stream<T>> mapStream(Function<? super T, ? extends U> mapper,
                                                      Matcher<Iterable<? super U>> matcher) {
        // FIXME: should be composable as asIterable(mapIterable(mapper, matcher)) - generics issue
        return mappedBy(stream -> stream.map(mapper::apply).collect(Collectors.toList()), matcher);
    }

    /**
     * Creates a {@link Matcher} that applies an iterable {@code matcher} on the input stream. It is an adapter method.
     * <p>
     * Example:
     * <pre>
     * assertThat(stream, toIterable(hasItem("Ana Pop"));
     * </pre>
     *
     * @param matcher The matcher to be applied on the resulting iterable.
     * @param <T>     The type of the elements in the input stream.
     * @since 0.11
     */
    public static <T> Matcher<Stream<T>> asIterable(Matcher<Iterable<? super T>> matcher) {
        return mappedBy(StreamSupportMatchers::streamToIterable, matcher);
    }

    /**
     * Creates a {@link Matcher} that checks if the given {@link BaseStream} is empty.
     * <p>
     * Examples:
     * <pre>
     * assertThat(Stream.empty(), emptyStream());
     * </pre>
     *
     * @param <T> The type of the stream elements.
     * @param <S> The type of the stream implementing {@code BaseStream}.
     * @since 0.11
     */
    public static <T, S extends BaseStream<T, S>> Matcher<BaseStream<T, S>> emptyStream() {
        return mappedBy(StreamSupportMatchers::baseStreamToIterable, emptyIterable());
    }

    private static <T> Iterable<T> streamToIterable(Stream<T> stream) {
        return baseStreamToIterable(stream);
    }

    private static <T, S extends BaseStream<T, S>> Iterable<T> baseStreamToIterable(BaseStream<T, S> stream) {
        List<T> target = new ArrayList<>();
        Iterators.forEachRemaining(stream.iterator(), target::add);
        return target;
    }

    static <E> org.hamcrest.Matcher<java.lang.Iterable<? extends E>> emptyIterable() {
        return MatcherBuilder.<Iterable<? extends E>>of(Iterable.class)
                .matches(it -> !it.iterator().hasNext())
                .description("an empty iterable")
                .describeMismatch((it, d) -> d.appendValueList("[", ",", "]", it))
                .build();
    }
}
