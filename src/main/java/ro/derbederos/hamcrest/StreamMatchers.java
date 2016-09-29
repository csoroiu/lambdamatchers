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

import static org.hamcrest.Matchers.emptyIterable;
import static ro.derbederos.hamcrest.LambdaMatchers.map;

/**
 * This class provides a set of mapping matchers for java 8 streams.
 * Basically it contains matchers that convert streams to {@link Iterable} and allow {@link Iterable}
 * matchers to be used.
 * <p>
 * Examples:
 * <pre>
 * assertThat(stream, mapStream(Person::getName, hasItem(startsWith("Ana"))));
 *
 * assertThat(stream, toIterable(hasItem("Ana Pop"));
 *
 * assertThat(Stream.empty(), emptyStream());
 * </pre>
 *
 * @since 0.1
 */
public final class StreamMatchers {

    private StreamMatchers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Utility method that creates a matcher that converts a stream of <code>&lt;T&gt;</code> to an iterable of
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
        return map(stream -> stream.map(mapper::apply).collect(Collectors.toList()), matcher);
    }

    /**
     * Creates a {@link Matcher} that applies an iterable <code>matcher</code> on the input stream. It is an adapter method.
     * <p>
     * Example:
     * <pre>
     * assertThat(stream, toIterable(hasItem("Ana Pop"));
     * </pre>
     *
     * @param matcher The matcher to be applied on the resulting iterable.
     * @param <T>     The type of the elements in the input stream.
     * @since 0.1
     */
    public static <T> Matcher<Stream<? extends T>> toIterable(Matcher<Iterable<? super T>> matcher) {
        return mapStream(a -> a, matcher);
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
