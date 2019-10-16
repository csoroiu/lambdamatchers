/*
 * Copyright (c) 2016-2018 Claudiu Soroiu
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.hasProperty;
import static ro.derbederos.hamcrest.LambdaMatchers.assertFeature;
import static ro.derbederos.hamcrest.LambdaMatchers.featureArray;
import static ro.derbederos.hamcrest.LambdaMatchers.featureIterable;
import static ro.derbederos.hamcrest.LambdaMatchers.hasFeature;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertDescription;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertMismatchDescription;
import static ro.derbederos.hamcrest.RegexMatchers.matchesPattern;
import static ro.derbederos.hamcrest.TypeResolverFeatureMatcherFactory.supplierMatcher;

public class LambdaMatchersTest {
    @Test
    public void simpleTestObjectMethodReference() {
        Person p = new Person("Alice", 21);
        assertThat(p, hasFeature(Person::getName, startsWith("A")));
    }

    @Test
    public void simpleTestObjectMethodReferenceDescription() {
        Matcher<Person> featureMatcher = hasFeature(Person::getName, startsWith("B"));
        assertDescription(equalTo("a Person having `Person::getName` a string starting with \"B\""), featureMatcher);
        assertMismatchDescription(equalTo("`Person::getName` was \"Alice\""),
                new Person("Alice", 21), featureMatcher);
    }

    private String getPersonName(Person p) {
        return p.getName();
    }

    @Test
    public void simpleTestInstanceObjectMethodReference() {
        Person p = new Person("Alice", 21);
        assertThat(p, hasFeature(this::getPersonName, startsWith("A")));
    }

    @Test
    public void simpleTestInstanceObjectMethodReferenceDescription() {
        Matcher<Person> featureMatcher = hasFeature(this::getPersonName, startsWith("B"));
        // retrolambda creates an access method for a private instance method reference
        assertDescription(anyOf(
                equalTo("a Person having `LambdaMatchersTest::getPersonName` a string starting with \"B\""),
                equalTo("a Person having `(String)LambdaMatchersTest::access$lambda$1` a string starting with \"B\""),
                equalTo("a Person having `(String)LambdaMatchersTest::access$lambda$simpleTestInstanceObjectMethodReferenceDescription$1` a string starting with \"B\"")),
                featureMatcher);
        assertMismatchDescription(anyOf(
                equalTo("`LambdaMatchersTest::getPersonName` was \"Alice\""),
                equalTo("`(String)LambdaMatchersTest::access$lambda$1` was \"Alice\""),
                equalTo("`(String)LambdaMatchersTest::access$lambda$simpleTestInstanceObjectMethodReferenceDescription$1` was \"Alice\"")),
                new Person("Alice", 21), featureMatcher);
    }

    @Test
    public void simpleTestConstructorReference() {
        assertThat("4", hasFeature(BigInteger::new, equalTo(BigInteger.valueOf(4L))));
    }

    @Test
    public void simpleTestConstructorReferenceDescription() {
        Matcher<String> featureMatcher = hasFeature(BigInteger::new, equalTo(BigInteger.valueOf(5L)));
        assertDescription(equalTo("a String having `BigInteger::new` <5>"), featureMatcher);
        assertMismatchDescription(equalTo("`BigInteger::new` was <4>"), "4", featureMatcher);
    }

    @Test
    public void simpleTestObjectClassMethodReference() {
        assertThat(4d, hasFeature(Object::toString, equalTo("4.0")));
    }

    @Test
    public void simpleTestObjectClassMethodReferenceDescription() {
        Matcher<Object> featureMatcher = hasFeature(Object::toString, equalTo("4"));
        assertDescription(equalTo("an Object having `Object::toString` \"4\""), featureMatcher);
        assertMismatchDescription(equalTo("`Object::toString` was \"4.0\""),
                4d, featureMatcher);
    }

    @Test
    public void simpleTestUnboxingMethodReference() {
        assertThat(4d, hasFeature(Double::doubleValue, equalTo(4.0)));
    }

    @Test
    @Disabled
    public void simpleTestUnboxingMethodReferenceDescription() {
        Matcher<Double> featureMatcher = hasFeature(Double::doubleValue, equalTo(4.0));
        assertDescription(equalTo("a Double having `Double::doubleValue` <4.0>"), featureMatcher);
        assertMismatchDescription(equalTo("`Double::doubleValue` was <5.0>"),
                5d, featureMatcher);
    }

    @SuppressWarnings({"rawtypes", "unchecked", "Convert2Lambda"})
    @Test
    public <T> void simpleTestAnonymousClassDescriptionUnknownFieldType() {
        @SuppressWarnings("Convert2Lambda")
        Function getAge = new Function<Person, T>() {
            @Override
            public T apply(Person person) {
                return (T) (Integer) person.getAge();
            }
        };
        Matcher<Person> featureMatcher = hasFeature(getAge, equalTo(22));
        assertDescription(equalTo("a Person having UnknownFieldType <22>"), featureMatcher);
        assertMismatchDescription(equalTo("UnknownFieldType was <21>"),
                new Person("Alice", 21), featureMatcher);
    }

    @SuppressWarnings({"rawtypes", "unchecked", "Convert2Lambda"})
    @Test
    public <T> void simpleTestAnonymousClassDescriptionUnknownObjectType() {
        Function getAge = new Function<T, Integer>() {
            @Override
            public Integer apply(T person) {
                return ((Person) person).getAge();
            }
        };
        Matcher<Person> featureMatcher = hasFeature(getAge, equalTo(22));
        assertDescription(equalTo("an UnknownObjectType having Integer <22>"), featureMatcher);
        assertMismatchDescription(equalTo("Integer was <21>"),
                new Person("Alice", 21), featureMatcher);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void simpleTestInvalidInputTypeDescription() {
        Matcher featureMatcher = hasFeature(Person::getAge, equalTo(22));
        assertDescription(equalTo("a Person having `Person::getAge` <22>"), featureMatcher);
        assertMismatchDescription(equalTo("was a java.lang.String (\"22\")"),
                "22", featureMatcher);
    }

    @Test
    public void simpleTestHasFeature() {
        Person p = new Person("Alice Bob", 21);
        Function<Person, String> featureFunction = a -> a.getName().split(" ")[1];
        assertThat(p, hasFeature(featureFunction, equalTo("Bob")));
    }

    @Test
    public void simpleTestHasFeatureDescription() {
        Function<Person, String> featureFunction = a -> a.getName().split(" ")[1];
        Matcher<Person> featureMatcher = hasFeature(featureFunction, equalTo("Pop"));
        assertDescription(matchesPattern("a Person having `\\(String\\)LambdaMatchersTest::lambda\\$simpleTestHasFeatureDescription\\$\\d+` \"Pop\""), featureMatcher);
        assertMismatchDescription(matchesPattern("`\\(String\\)LambdaMatchersTest::lambda\\$simpleTestHasFeatureDescription\\$\\d+` was \"Bob\""),
                new Person("Alice Bob", 21), featureMatcher);
    }

    @Test
    public void simpleTestHasProperty() {
        Person p = new Person("Alice Bob", 21);
        assertThat(p, hasProperty("age", equalTo(21)));
    }

    @Test
    public void simpleNotMatcherHasFeatureLambda() {
        Person p = new Person("Alice", 21);
        assertThat(p, not(hasFeature(Person::getName, startsWith("B"))));
    }

    @Test
    public void simpleBetterNotMatcherHasFeatureLambda() {
        Person p = new Person("Alice", 21);
        assertThat(p, hasFeature(Person::getName, not(startsWith("B"))));
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
        assertThat(list, everyItem(hasFeature(Person::getAge, equalTo(21))));
    }

    @Test
    public void listTestHasFeature() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Bob", 21),
                new Person("Ariana Bob", 21));
        Function<Person, String> featureFunction = a -> a.getName().split(" ")[1];
        assertThat(list, everyItem(hasFeature(featureFunction, equalTo("Bob"))));
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
        assertThat(list, not(everyItem(hasFeature(Person::getName, startsWith("Alice")))));
    }

    @Test
    public void listBetterNotMatcherTestMethodReference() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        assertThat(list, everyItem(hasFeature(Person::getName, not(startsWith("Alices")))));
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
        assertThat(list, hasItem(hasFeature(Person::getName, startsWith("Alice"))));
    }

    @Test
    public void listHasItemMatcherTestHasFeature() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        // in case of inlining the following line, an explicit cast is needed
        Function<Person, Integer> featureFunction = (person) -> person.getAge() + 1;
        assertThat(list, hasItem(hasFeature(featureFunction, equalTo(22))));
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
        Person[] array = {new Person("Alice Bob", 21), new Person("Ana Pop", 21),
                new Person("Ariana G", 21)};
        assertThat(array, hasItemInArray(hasFeature(Person::getName, startsWith("Alice"))));
    }

    @Test
    public void arrayHasItemMatcherTestHasFeature() {
        Person[] array = {new Person("Alice Bob", 21), new Person("Ana Pop", 21),
                new Person("Ariana G", 21)};
        Function<Person, Integer> featureFunction = (person) -> person.getAge() + 1;
        assertThat(array, hasItemInArray(hasFeature(featureFunction, equalTo(22))));
    }

    @Test
    public void arrayHasItemMatcherTestHasProperty() {
        Person[] array = {new Person("Alice Bob", 21), new Person("Ana Pop", 21),
                new Person("Ariana G", 21)};
        assertThat(array, hasItemInArray(hasProperty("age", equalTo(21))));
    }

    @Test
    public void listHasItemMatcherTestFeatureIterable() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        assertThat(list, featureIterable(Person::getName, hasItem("Ana Pop")));
    }

    @Test
    public void listHasItemMatcherTestFeatureIterableDescription() {
        List<Person> list = Arrays.asList(new Person("Alice Bob", 21),
                new Person("Ana Pop", 21),
                new Person("Ariana G", 21));
        Matcher<Iterable<Person>> featureMatcher = featureIterable(Person::getName, hasItem("Ana Pop1"));
        assertDescription(matchesPattern("an Iterable having `\\(Iterable\\)LambdaMatchers::lambda\\$featureIterable\\$\\d+` a collection containing \"Ana Pop1\""), featureMatcher);
        assertMismatchDescription(matchesPattern("`\\(Iterable\\)LambdaMatchers::lambda\\$featureIterable\\$\\d+` mismatches were: \\[was \"Alice Bob\", was \"Ana Pop\", was \"Ariana G\"\\]"),
                list, featureMatcher);
    }

    @Test
    public void arrayHasItemMatcherTestFeatureArray() {
        Person[] array = {new Person("Alice Bob", 21), new Person("Ana Pop", 21), new Person("Ariana G", 21)};
        assertThat(array, featureArray(Person::getName, hasItem(startsWith("Ana"))));
    }

    @Test
    public void arrayHasItemMatcherTestFeatureArrayDescription() {
        Person[] array = {new Person("Alice Bob", 21), new Person("Ana Pop", 21), new Person("Ariana G", 21)};
        Matcher<Person[]> featureMatcher = featureArray(Person::getName, hasItem(startsWith("Ana1")));
        assertDescription(matchesPattern("an Object\\[\\] having `\\(Iterable\\)LambdaMatchers::lambda\\$featureArray\\$\\d+` a collection containing a string starting with \"Ana1\""), featureMatcher);
        assertMismatchDescription(matchesPattern("`\\(Iterable\\)LambdaMatchers::lambda\\$featureArray\\$\\d+` mismatches were: \\[was \"Alice Bob\", was \"Ana Pop\", was \"Ariana G\"\\]"),
                array, featureMatcher);
    }

    @Test
    public void assertFeatureSimpleTestObjectMethodReference() {
        Person p = new Person("Brutus", 21);
        assertFeature(p::getName, equalTo("Brutus"));
    }

    @Test
    public void assertFeatureSimpleTestObjectMethodReferenceDescription() {
        Person p = new Person("Brutus", 21);
        Supplier<String> supplier = p::getName;
        Matcher<Supplier<String>> matcher = supplierMatcher(supplier, equalTo("Caesar"));

        assertDescription(equalTo("a `Person::getName` \"Caesar\""), matcher);
        assertMismatchDescription(equalTo("`Person::getName` was \"Brutus\""),
                supplier, matcher);
    }

    @SuppressWarnings("WeakerAccess")
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
