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

public final class LambdaMatchers {

    private LambdaMatchers() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static <T, U> Matcher<T> map(Function<T, U> mapper, Matcher<? super U> matcher) {
        return FunctionMatcher.map(mapper, matcher);
    }

    public static <T, U> Matcher<Iterable<? extends T>> mapIterable(Function<T, U> mapper,
            Matcher<Iterable<? super U>> matcher) {
        return map(iter -> (Iterable<? super U>) StreamSupport.stream(iter.spliterator(), false).map(mapper)
                .collect(Collectors.toList()), matcher);
    }

    public static <T, U> Matcher<T[]> mapArray(Function<T, U> mapper, Matcher<Iterable<? super U>> matcher) {
        return map(array -> (Iterable<? super U>) Stream.of(array).map(mapper).collect(Collectors.toList()), matcher);
    }

    public static <T, U> Matcher<Stream<? extends T>> mapStream(Function<T, U> mapper,
            Matcher<Iterable<? super U>> matcher) {
        return map(stream -> (Iterable<? super U>) stream.map(mapper).collect(Collectors.toList()), matcher);
    }

    public static <T> Matcher<Stream<? extends T>> toIterable(Matcher<Iterable<? super T>> matcher) {
        return mapStream(a -> a, matcher);
    }

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
