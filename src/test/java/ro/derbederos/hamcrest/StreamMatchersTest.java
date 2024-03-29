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
import org.junit.jupiter.api.Test;
import ro.derbederos.hamcrest.LambdaMatchersTest.Person;

import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertDescription;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertMismatchDescription;
import static ro.derbederos.hamcrest.RetryMatchers.retry;
import static ro.derbederos.hamcrest.StreamMatchers.emptyStream;
import static ro.derbederos.hamcrest.StreamMatchers.featureStream;
import static ro.derbederos.hamcrest.StreamMatchers.toIterable;

public class StreamMatchersTest {

    @Test
    public void streamIsEmpty() {
        assertThat(Stream.empty(), emptyStream());
    }

    @Test
    public void streamIsEmptyDescription() {
        assertDescription(endsWith("an empty stream"), emptyStream());
        assertMismatchDescription(endsWith("[\"alabala\",\"trilulilu\"]"),
                Stream.of("alabala", "trilulilu"), emptyStream());
    }

    @Test
    public void longStreamIsEmpty() {
        assertThat(LongStream.empty(), emptyStream());
    }

    @Test
    public void longStreamIsEmptyDescription() {
        assertDescription(endsWith("an empty stream"), emptyStream());
        assertMismatchDescription(endsWith("[<3L>,<5L>]"),
                LongStream.of(3, 5), emptyStream());
    }

    @Test
    public void streamHasItemMatcherTestFeatureStream() {
        Stream<Person> stream = Stream.of(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        assertThat(stream, featureStream(Person::getName, hasItem("Ana Pop")));
    }

    @Test
    public void streamHasItemMatcherTestFeatureStreamDescription() {
        Stream<Person> stream = Stream.of(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        Matcher<Stream<Person>> streamMatcher = featureStream(Person::getName, hasItem("Ana Pop1"));
        assertDescription(endsWith("a collection containing \"Ana Pop1\""), streamMatcher);
        assertMismatchDescription(containsString("was \"Alice Bob\", was \"Ana Pop\", was \"Ariana G\""),
                stream, streamMatcher);
    }

    @Test
    public void streamHasItemMatcherTestToIterable() {
        Stream<String> stream = Stream.of("Alice Bob", "Ana Pop", "Ariana G");
        assertThat(stream, toIterable(hasItem("Ana Pop")));
    }

    @Test
    public void streamHasItemMatcherTestToIterableDescription() {
        Stream<String> stream = Stream.of("Alice Bob", "Ana Pop", "Ariana G");
        Matcher<Stream<String>> streamMatcher = toIterable(hasItem("Ana Pop1"));
        assertDescription(endsWith("a collection containing \"Ana Pop1\""), streamMatcher);
        assertMismatchDescription(containsString("was \"Alice Bob\", was \"Ana Pop\", was \"Ariana G\""),
                stream, streamMatcher);
    }

    @Test
    public void retryStreamTest() {
        Stream<String> stream = Stream.of("aa", "bb", "cc");
        assertThat(stream, not(retry(500, toIterable(hasItem("aaa")))));
    }
}
