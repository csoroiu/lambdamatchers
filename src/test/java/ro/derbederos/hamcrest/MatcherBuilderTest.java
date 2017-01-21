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

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;
import ro.derbederos.hamcrest.LambdaMatchersTest.Person;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertDescription;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertMismatchDescription;

public class MatcherBuilderTest {

    @Test
    public void customEqualsMatcher() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        Person p1 = new Person("Alice Bob", 0);
        Person p2 = new Person("Ana Pop", 0);
        Person p3 = new Person("Ariana G", 0);
        assertThat(list, hasItems(eqMatcherPredicate(p1), eqMatcherPredicate(p2), eqMatcherPredicate(p3)));
    }

    @SafeVarargs //delegate method call using @SafeVarargs to fix the compilation warning
    private static <T> Matcher<Iterable<T>> hasItems(Matcher<? super T>... itemMatchers) {
        return Matchers.hasItems(itemMatchers);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWrongType() {
        Person expected = new Person("Alice Bob", 21);
        Matcher matcher = eqMatcherBiPredicate(expected);
        assertThat("alabala", not(matcher));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWrongTypeDescription() {
        Person expected = new Person("Alice Bob", 21);
        Matcher matcher = eqMatcherBiPredicate(expected);
        assertDescription(equalTo("Alice Bob"), matcher);
        assertMismatchDescription(equalTo("was a java.lang.String (\"alabala\")"),
                "alabala", matcher);
    }

    public Matcher<Person> eqMatcherPredicate(Person expected) {
        return MatcherBuilder.of(Person.class)
                .matches(value -> myCustomEquals(expected, value))
                .description(expected::getName).describeMismatch(actual -> "was " + actual.getName()).build();
    }

    public Matcher<Person> eqMatcherBiPredicate(Person expected) {
        return MatcherBuilder.of(Person.class).matches(expected, MatcherBuilderTest::myCustomEquals)
                .description(expected::getName).describeMismatch(actual -> "was " + actual.getName()).build();
    }

    private static boolean myCustomEquals(Person p1, Person p2) {
        return p1.getName().equals(p2.getName());
    }
}
