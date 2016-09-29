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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ro.derbederos.hamcrest.LambdaMatchersTest.Person;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static ro.derbederos.hamcrest.StreamMatchers.*;

public class StreamMatchersTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void streamIsEmpty() {
        assertThat(Stream.empty(), emptyStream());
    }

    @Test
    public void streamIsEmptyAssertionError() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("an empty iterable");
        expectedException.expectMessage("[\"alabala\",\"trilulilu\"]");

        assertThat(Stream.of("alabala", "trilulilu"), emptyStream());
    }

    @Test
    public void streamHasItemMatcherTestMapStream() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        Stream<Person> stream = Stream.of(p0, p1, p2);

        assertThat(stream, mapStream(Person::getName, hasItem("Ana Pop")));
    }

    @Test
    public void streamHasItemMatcherTestMapStreamAssertionError() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("a collection containing \"Ana Pop1\"");
        expectedException.expectMessage("was \"Alice Bob\", was \"Ana Pop\", was \"Ariana G\"");

        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        Stream<Person> stream = Stream.of(p0, p1, p2);

        assertThat(stream, mapStream(Person::getName, hasItem("Ana Pop1")));
    }

    @Test
    public void streamHasItemMatcherTestToIterable() {
        Stream<String> stream = Stream.of("Alice Bob", "Ana Pop", "Ariana G");

        assertThat(stream, toIterable(hasItem("Ana Pop")));
    }

    @Test
    public void streamHasItemMatcherTestToIterableAssertionError() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("a collection containing \"Ana Pop1\"");
        expectedException.expectMessage("was \"Alice Bob\", was \"Ana Pop\", was \"Ariana G\"");

        Stream<String> stream = Stream.of("Alice Bob", "Ana Pop", "Ariana G");

        assertThat(stream, toIterable(hasItem("Ana Pop1")));
    }
}
