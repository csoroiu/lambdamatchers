package ro.derbederos.hamcrest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ro.derbederos.hamcrest.LambdaMatchers.*;

public class LambdaMatchersTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void simpleTestMethodReference() {
        Person p = new Person("Alice", 21);
        assertThat(p, map(Person::getName, startsWith("A")));
    }

    @Test
    public void simpleTestMethodReferenceAssertionError() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: a person with string property a string starting with \"B\"");
        expectedException.expectMessage("     but: string was \"Alice\"");
        Person p = new Person("Alice", 21);
        assertThat(p, map(Person::getName, startsWith("B")));
    }

    @Test
    public void simpleTestLambda() {
        Person p = new Person("Alice Bob", 21);
        Function<Person, String> mapper = a -> a.getName().split(" ")[1];
        assertThat(p, map(mapper, equalTo("Bob")));
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

        //in case of inlining the following line, an explicit cast is needed
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
