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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ro.derbederos.hamcrest.LambdaMatchers.*;

public class LambdaMatchersTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void simpleTestObjectMethodReference() {
        Person p = new Person("Alice", 21);
        assertThat(p, map(Person::getName, startsWith("A")));
    }

    @Test
    public void simpleTestObjectMethodReferenceAssertionError() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: a Person having `String Person.getName()` a string starting with \"B\"");
        expectedException.expectMessage("     but: `String Person.getName()` was \"Alice\"");

        Person p = new Person("Alice", 21);
        assertThat(p, map(Person::getName, startsWith("B")));
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
    public void simpleTestInstanceMethodReferenceAssertionError() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: a Person having `String LambdaMatchersTest.getPersonName(Person)` a string starting with \"B\"");
        expectedException.expectMessage("     but: `String LambdaMatchersTest.getPersonName(Person)` was \"Alice\"");

        Person p = new Person("Alice", 21);
        assertThat(p, map(this::getPersonName, startsWith("B")));
    }

    @Test
    public void simpleTestConstructorReference() {
        assertThat("4", map(Integer::new, equalTo(4)));
    }

    @Test
    @Ignore //ignore until constructor reference is released in typetools
    public void simpleTestConstructorReferenceAssertionError() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: a String having `new Integer(String)` <5>");
        expectedException.expectMessage("     but: `new Integer(String)` was <4>");

        assertThat("4", map(Integer::new, equalTo(5)));
    }

    @Test
    public void simpleTestObjectClassMethodReference() {
        assertThat(4d, map(Object::toString, equalTo("4.0")));
    }

    @Test
    public void simpleTestObjectClassMethodReferenceAssertionError() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: an Object having `String Object.toString()` \"4\"");
        expectedException.expectMessage("     but: `String Object.toString()` was \"4.0\"");

        assertThat(4d, map(Object::toString, equalTo("4")));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public <T> void simpleTestAnonymousClassAssertionErrorUnknownFieldType() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: a Person having UnknownFieldType <22>");
        expectedException.expectMessage("     but: UnknownFieldType was <21>");

        Function getAge = new Function<Person, T>() {
            @Override
            public T apply(Person person) {
                return (T) (Integer) person.getAge();
            }
        };

        Person p = new Person("Alice", 21);
        assertThat(p, map(getAge, equalTo(22)));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public <T> void simpleTestAnonymousClassAssertionErrorUnknownObjectType() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: an UnknownObjectType having Integer <22>");
        expectedException.expectMessage("     but: Integer was <21>");

        Function getAge = new Function<T, Integer>() {
            @Override
            public Integer apply(T person) {
                return ((Person) person).getAge();
            }
        };

        Person p = new Person("Alice", 21);
        assertThat(p, map(getAge, equalTo(22)));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void simpleTestInvalidInputTypeAssertionError() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: a Person having `int Person.getAge()` <22>");
        expectedException.expectMessage("     but: was a java.lang.String (\"22\")");

        Matcher matcher = map(Person::getAge, equalTo(22));
        assertThat("22", matcher);
    }

    @Test
    public void simpleTestLambda() {
        Person p = new Person("Alice Bob", 21);
        Function<Person, String> mapper = a -> a.getName().split(" ")[1];
        assertThat(p, map(mapper, equalTo("Bob")));
    }

    @Test
    public void simpleTestLambdaAssertionError() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: a Person having `String lambda$(Person)` \"Pop\"");
        expectedException.expectMessage("     but: `String lambda$(Person)` was \"Bob\"");

        Person p = new Person("Alice Bob", 21);
        Function<Person, String> mapper = a -> a.getName().split(" ")[1];
        assertThat(p, map(mapper, equalTo("Pop")));
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
        Person p0 = new Person("Alice", 21);
        Person p1 = new Person("Ana", 21);
        Person p2 = new Person("Ariana", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        assertThat(list, everyItem(map(Person::getAge, equalTo(21))));
    }

    @Test
    public void listTestLambda() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Bob", 21);
        Person p2 = new Person("Ariana Bob", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        Function<Person, String> mapper = a -> a.getName().split(" ")[1];
        assertThat(list, everyItem(map(mapper, equalTo("Bob"))));
    }

    @Test
    public void listTestHasProperty() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Bob", 21);
        Person p2 = new Person("Ariana Bob", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        assertThat(list, everyItem(hasProperty("age", equalTo(21))));
    }

    @Test
    public void listNotMatcherTestMethodReference() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        assertThat(list, not(everyItem(map(Person::getName, startsWith("Alice")))));
    }

    @Test
    public void listBetterNotMatcherTestMethodReference() {
        Person p0 = new Person("Alic Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        assertThat(list, everyItem(map(Person::getName, not(startsWith("Alice")))));
    }

    @Test
    public void listBetterNotMatcherHasProperty() {
        Person p0 = new Person("Alic Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        assertThat(list, everyItem(hasProperty("name", not(startsWith("Alice")))));
    }

    @Test
    public void listHasItemMatcherTestMethodReference() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        assertThat(list, hasItem(map(Person::getName, startsWith("Alice"))));
    }

    @Test
    public void listHasItemMatcherTestLambda() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        // in case of inlining the following line, an explicit cast is needed
        Function<Person, Integer> mapper = (person) -> person.getAge() + 1;
        assertThat(list, hasItem(map(mapper, equalTo(22))));
    }

    @Test
    public void listHasItemMatcherTestHasProperty() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        assertThat(list, hasItem(hasProperty("age", equalTo(21))));
    }

    @Test
    public void arrayHasItemMatcherTestMethodReference() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        Person[] array = {p0, p1, p2};

        assertThat(array, hasItemInArray(map(Person::getName, startsWith("Alice"))));
    }

    @Test
    public void arrayHasItemMatcherTestLambda() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        Person[] array = {p0, p1, p2};

        Function<Person, Integer> mapper = (person) -> person.getAge() + 1;
        assertThat(array, hasItemInArray(map(mapper, equalTo(22))));
    }

    @Test
    public void arrayHasItemMatcherTestHasProperty() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        Person[] array = {p0, p1, p2};

        assertThat(array, hasItemInArray(hasProperty("age", equalTo(21))));
    }

    @Test
    public void listHasItemMatcherTestMapIterable() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        assertThat(list, mapIterable(Person::getName, hasItem("Ana Pop")));
    }

    @Test
    public void listHasItemMatcherTestMapIterableAssertionError() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("a collection containing \"Ana Pop1\"");
        expectedException.expectMessage("was \"Alice Bob\", was \"Ana Pop\", was \"Ariana G\"");

        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        assertThat(list, mapIterable(Person::getName, hasItem("Ana Pop1")));
    }

    @Test
    public void arrayHasItemMatcherTestMapArray() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        Person[] array = {p0, p1, p2};

        assertThat(array, mapArray(Person::getName, hasItem(startsWith("Ana"))));
    }

    @Test
    public void arrayHasItemMatcherTestMapArrayAssertionError() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("a collection containing a string starting with \"Ana1\"");
        expectedException.expectMessage("was \"Alice Bob\", was \"Ana Pop\", was \"Ariana G\"");

        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        Person[] array = {p0, p1, p2};

        assertThat(array, mapArray(Person::getName, hasItem(startsWith("Ana1"))));
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
