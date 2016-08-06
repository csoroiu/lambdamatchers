package ro.derbederos.hamcrest;

import org.junit.Test;

import lombok.Value;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static ro.derbederos.hamcrest.LambdaMatchers.*;

public class LambdaMatchersTest {

    @Test
    public void simpleTestMethodReference() {
        Person p = new Person("Alice", 21);
        assertThat(p, mappedItem(Person::getName, startsWith("A")));
    }

    @Test
    public void simpleTestLambda() {
        Person p = new Person("Alice Bob", 21);
        Function<Person, String> mapper = a -> a.getName().split(" ")[1];
        assertThat(p, mappedItem(mapper, equalTo("Bob")));
    }

    @Test
    public void simpleTestHasProperty() {
        Person p = new Person("Alice Bob", 21);
        assertThat(p, hasProperty("age", equalTo(21)));
    }

    @Test
    public void simpleNotMatcherTestLambda() {
        Person p = new Person("Alice", 21);
        assertThat(p, not(mappedItem(Person::getName, startsWith("B"))));
    }

    @Test
    public void simpleBetterNotMatcherTestLambda() {
        Person p = new Person("Alice", 21);
        assertThat(p, mappedItem(Person::getName, not(startsWith("B"))));
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

        assertThat(list, everyItem(mappedItem(Person::getAge, equalTo(21))));
    }

    @Test
    public void listTestLambda() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Bob", 21);
        Person p2 = new Person("Ariana Bob", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        Function<Person, String> mapper = a -> a.getName().split(" ")[1];
        assertThat(list, everyItem(mappedItem(mapper, equalTo("Bob"))));
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

        assertThat(list, not(everyItem(mappedItem(Person::getName, startsWith("Alice")))));
    }

    @Test
    public void listBetterNotMatcherTestMethodReference() {
        Person p0 = new Person("Alic Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        assertThat(list, everyItem(mappedItem(Person::getName, not(startsWith("Alice")))));
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

        assertThat(list, hasItem(mappedItem(Person::getName, startsWith("Alice"))));
    }

    @Test
    public void listHasItemMatcherTestLambda() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        Function<Person, Integer> mapper = (person) -> person.getAge() + 1; //in case of inlining this, an explicit cast is needed
        assertThat(list, hasItem(mappedItem(mapper, equalTo(22))));
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

        assertThat(array, hasItemInArray(mappedItem(Person::getName, startsWith("Alice"))));
    }

    @Test
    public void arrayHasItemMatcherTestLambda() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        Person[] array = {p0, p1, p2};

        Function<Person, Integer> mapper = (person) -> person.getAge() + 1;
        assertThat(array, hasItemInArray(mappedItem(mapper, equalTo(22))));
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
    public void listHasItemMatcherTestMappedWith() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        List<Person> list = Arrays.asList(p0, p1, p2);

        assertThat(list, mappedIterable(Person::getName, hasItem("Ana Pop")));
    }

    @Test
    public void arrayHasItemMatcherTestMappedWith() {
        Person p0 = new Person("Alice Bob", 21);
        Person p1 = new Person("Ana Pop", 21);
        Person p2 = new Person("Ariana G", 21);
        Person[] array = {p0, p1, p2};

        assertThat(array, mappedArray(Person::getName, hasItem(startsWith("Ana"))));
    }

    @Value
    public static class Person {
        String name;
        int age;
    }
}
