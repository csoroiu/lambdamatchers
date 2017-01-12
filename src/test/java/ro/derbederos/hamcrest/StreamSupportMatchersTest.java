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

import java8.util.stream.RefStreams;
import java8.util.stream.Stream;
import org.hamcrest.Matcher;
import org.junit.Test;
import ro.derbederos.hamcrest.LambdaMatchersTest.Person;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertDescription;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertMismatchDescription;
import static ro.derbederos.hamcrest.StreamSupportMatchers.*;

public class StreamSupportMatchersTest {

    @Test
    public void streamIsEmpty() {
        assertThat(RefStreams.empty(), emptyStream());
    }

    @Test
    public void streamIsEmptyDescription() {
        assertDescription(endsWith("an empty iterable"), emptyStream());
        assertMismatchDescription(endsWith("[\"alabala\",\"trilulilu\"]"),
                RefStreams.of("alabala", "trilulilu"), emptyStream());
    }

    @Test
    public void streamHasItemMatcherTestMapStream() {
        Stream<Person> stream = RefStreams.of(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        assertThat(stream, mapStream(Person::getName, hasItem("Ana Pop")));
    }

    @Test
    public void streamHasItemMatcherTestMapStreamDescription() {
        Stream<Person> stream = RefStreams.of(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        Matcher<Stream<Person>> streamMatcher = mapStream(Person::getName, hasItem("Ana Pop1"));
        assertDescription(endsWith("a collection containing \"Ana Pop1\""), streamMatcher);
        assertMismatchDescription(containsString("was \"Alice Bob\", was \"Ana Pop\", was \"Ariana G\""),
                stream, streamMatcher);
    }

    @Test
    public void streamHasItemMatcherTestToIterable() {
        Stream<String> stream = RefStreams.of("Alice Bob", "Ana Pop", "Ariana G");
        assertThat(stream, asIterable(hasItem("Ana Pop")));
    }

    @Test
    public void streamHasItemMatcherTestToIterableDescription() {
        Stream<String> stream = RefStreams.of("Alice Bob", "Ana Pop", "Ariana G");
        Matcher<Stream<String>> streamMatcher = asIterable(hasItem("Ana Pop1"));
        assertDescription(endsWith("a collection containing \"Ana Pop1\""), streamMatcher);
        assertMismatchDescription(containsString("was \"Alice Bob\", was \"Ana Pop\", was \"Ariana G\""),
                stream, streamMatcher);
    }
}