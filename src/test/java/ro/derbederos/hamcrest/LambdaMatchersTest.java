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
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ro.derbederos.hamcrest.LambdaMatchers.*;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertDescription;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertMismatchDescription;

public class LambdaMatchersTest {
    @Test
    public void simpleTestObjectMethodReference() {
        Person p = new Person("Alice", 21);
        assertThat(p, map(Person::getName, startsWith("A")));
    }

    @Test
    public void simpleTestObjectMethodReferenceDescription() {
        Matcher<Person> mapMatcher = map(Person::getName, startsWith("B"));
        assertDescription(equalTo("a Person having `String Person.getName()` a string starting with \"B\""), mapMatcher);
        assertMismatchDescription(equalTo("`String Person.getName()` was \"Alice\""),
                new Person("Alice", 21), mapMatcher);
    }

    private String getPersonName(Person p) {
        return p.getName();
    }

    @Test
    public void simpleTestInstanceObjectMethodReference() {
        Person p = new Person("Alice", 21);
        assertThat(p, map(this::getPersonName, startsWith("A")));
    }

    @Test
    public void simpleTestInstanceObjectMethodReferenceDescription() {
        Matcher<Person> mapMatcher = map(this::getPersonName, startsWith("B"));
        assertDescription(anyOf(
                equalTo("a Person having `String LambdaMatchersTest.getPersonName(Person)` a string starting with \"B\""),
                equalTo("a Person having `String lambda$(LambdaMatchersTest, Person)` a string starting with \"B\"")),
                mapMatcher);
        assertMismatchDescription(anyOf(
                equalTo("`String LambdaMatchersTest.getPersonName(Person)` was \"Alice\""),
                equalTo("`String lambda$(LambdaMatchersTest, Person)` was \"Alice\"")),
                new Person("Alice", 21), mapMatcher);
    }

    @Test
    public void simpleTestConstructorReference() {
        assertThat("4", map(Integer::new, equalTo(4)));
    }

    @Test
    @Ignore //ignore until constructor reference is released in typetools
    public void simpleTestConstructorReferenceDescription() {
        Matcher<String> mapMatcher = map(Integer::new, equalTo(5));
        assertDescription(equalTo("a String having `new Integer(String)` <5>"), mapMatcher);
        assertMismatchDescription(equalTo("`new Integer(String)` was <4>"), "4", mapMatcher);
    }

    @Test
    public void simpleTestObjectClassMethodReference() {
        assertThat(4d, map(Object::toString, equalTo("4.0")));
    }

    @Test
    public void simpleTestObjectClassMethodReferenceDescription() {
        Matcher<Object> mapMatcher = map(Object::toString, equalTo("4"));
        assertDescription(equalTo("an Object having `String Object.toString()` \"4\""), mapMatcher);
        assertMismatchDescription(equalTo("`String Object.toString()` was \"4.0\""),
                4d, mapMatcher);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public <T> void simpleTestAnonymousClassDescriptionUnknownFieldType() {
        Function getAge = new Function<Person, T>() {
            @Override
            public T apply(Person person) {
                return (T) (Integer) person.getAge();
            }
        };
        Matcher<Person> mapMatcher = map(getAge, equalTo(22));
        assertDescription(equalTo("a Person having UnknownFieldType <22>"), mapMatcher);
        assertMismatchDescription(equalTo("UnknownFieldType was <21>"),
                new Person("Alice", 21), mapMatcher);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public <T> void simpleTestAnonymousClassDescriptionUnknownObjectType() {
        Function getAge = new Function<T, Integer>() {
            @Override
            public Integer apply(T person) {
                return ((Person) person).getAge();
            }
        };
        Matcher<Person> mapMatcher = map(getAge, equalTo(22));
        assertDescription(equalTo("an UnknownObjectType having Integer <22>"), mapMatcher);
        assertMismatchDescription(equalTo("Integer was <21>"),
                new Person("Alice", 21), mapMatcher);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void simpleTestInvalidInputTypeDescription() {
        Matcher mapMatcher = map(Person::getAge, equalTo(22));
        assertDescription(equalTo("a Person having `int Person.getAge()` <22>"), mapMatcher);
        assertMismatchDescription(equalTo("was a java.lang.String (\"22\")"),
                "22", mapMatcher);
    }

    @Test
    public void simpleTestLambda() {
        Person p = new Person("Alice Bob", 21);
        Function<Person, String> mapper = a -> a.getName().split(" ")[1];
        assertThat(p, map(mapper, equalTo("Bob")));
    }

    @Test
    public void simpleTestLambdaDescription() {
        Function<Person, String> mapper = a -> a.getName().split(" ")[1];
        Matcher<Person> mapMatcher = map(mapper, equalTo("Pop"));
        assertDescription(equalTo("a Person having `String lambda$(Person)` \"Pop\""), mapMatcher);
        assertMismatchDescription(equalTo("`String lambda$(Person)` was \"Bob\""),
                new Person("Alice Bob", 21), mapMatcher);
    }

    @Test
    public void simpleTestHasProperty() {
        Person p = new Person("Alice Bob", 21);
        assertThat(p, hasProperty("age", equalTo(21)));
    }

    @Test
    public void simpleNotMatcherTestLambda() {
        Person p = new Person("Alice", 21);
        assertThat(p, not(map(Person::getName, startsWith("B"))));
    }

    @Test
    public void simpleBetterNotMatcherTestLambda() {
        Person p = new Person("Alice", 21);
        assertThat(p, map(Person::getName, not(startsWith("B"))));
    }

    @Test
    public void simpleBetterNotHasProperty() {
        Person p = new Person("Alice Bob", 21);
        assertThat(p, hasProperty("name", not(startsWith("B"))));
    }

    @Test
    public void listTestMethodReference() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        assertThat(list, everyItem(map(Person::getAge, equalTo(21))));
    }

    @Test
    public void listTestLambda() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Bob", 21),
                new Person("Ariana Bob", 21));
        Function<Person, String> mapper = a -> a.getName().split(" ")[1];
        assertThat(list, everyItem(map(mapper, equalTo("Bob"))));
    }

    @Test
    public void listTestHasProperty() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        assertThat(list, everyItem(hasProperty("age", equalTo(21))));
    }

    @Test
    public void listNotMatcherTestMethodReference() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        assertThat(list, not(everyItem(map(Person::getName, startsWith("Alice")))));
    }

    @Test
    public void listBetterNotMatcherTestMethodReference() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        assertThat(list, everyItem(map(Person::getName, not(startsWith("Alices")))));
    }

    @Test
    public void listBetterNotMatcherHasProperty() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        assertThat(list, everyItem(hasProperty("name", not(startsWith("Alices")))));
    }

    @Test
    public void listHasItemMatcherTestMethodReference() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        assertThat(list, hasItem(map(Person::getName, startsWith("Alice"))));
    }

    @Test
    public void listHasItemMatcherTestLambda() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        // in case of inlining the following line, an explicit cast is needed
        Function<Person, Integer> mapper = (person) -> person.getAge() + 1;
        assertThat(list, hasItem(map(mapper, equalTo(22))));
    }

    @Test
    public void listHasItemMatcherTestHasProperty() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        assertThat(list, hasItem(hasProperty("age", equalTo(21))));
    }

    @Test
    public void arrayHasItemMatcherTestMethodReference() {
        Person[] array = {new Person("Alice Bob", 21), new Person("Ana Pop", 21), new Person("Ariana G", 21)};
        assertThat(array, hasItemInArray(map(Person::getName, startsWith("Alice"))));
    }

    @Test
    public void arrayHasItemMatcherTestLambda() {
        Person[] array = {new Person("Alice Bob", 21), new Person("Ana Pop", 21), new Person("Ariana G", 21)};
        Function<Person, Integer> mapper = (person) -> person.getAge() + 1;
        assertThat(array, hasItemInArray(map(mapper, equalTo(22))));
    }

    @Test
    public void arrayHasItemMatcherTestHasProperty() {
        Person[] array = {new Person("Alice Bob", 21), new Person("Ana Pop", 21), new Person("Ariana G", 21)};
        assertThat(array, hasItemInArray(hasProperty("age", equalTo(21))));
    }

    @Test
    public void listHasItemMatcherTestMapIterable() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        assertThat(list, mapIterable(Person::getName, hasItem("Ana Pop")));
    }

    @Test
    public void listHasItemMatcherTestMapIterableDescription() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        Matcher<Iterable<? extends Person>> mapMatcher = mapIterable(Person::getName, hasItem("Ana Pop1"));
        assertDescription(equalTo("an Iterable having `Iterable lambda$(Function, Iterable)` a collection containing \"Ana Pop1\""), mapMatcher);
        assertMismatchDescription(equalTo("`Iterable lambda$(Function, Iterable)` was \"Alice Bob\", was \"Ana Pop\", was \"Ariana G\""),
                list, mapMatcher);
    }

    @Test
    public void arrayHasItemMatcherTestMapArray() {
        Person[] array = {new Person("Alice Bob", 21), new Person("Ana Pop", 21), new Person("Ariana G", 21)};
        assertThat(array, mapArray(Person::getName, hasItem(startsWith("Ana"))));
    }

    @Test
    public void arrayHasItemMatcherTestMapArrayDescription() {
        Person[] array = {new Person("Alice Bob", 21), new Person("Ana Pop", 21), new Person("Ariana G", 21)};
        Matcher<Person[]> mapMatcher = mapArray(Person::getName, hasItem(startsWith("Ana1")));
        assertDescription(equalTo("an Object[] having `Iterable lambda$(Function, Object[])` a collection containing a string starting with \"Ana1\""), mapMatcher);
        assertMismatchDescription(equalTo("`Iterable lambda$(Function, Object[])` was \"Alice Bob\", was \"Ana Pop\", was \"Ariana G\""),
                array, mapMatcher);
    }

    public static class Person {
        private final String name;
        private final int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }
}
