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

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static ro.derbederos.hamcrest.MappedValueMatcher.supplierMatcher;

/**
 * This class provides a set of mapping matchers based on java 8 functional interfaces (lambdas).
 * <p>
 * They are useful in plenty of places where you are required to make assertions on different properties of objects.
 * <p>
 * It can replace {@link org.hamcrest.Matchers#hasProperty(String, Matcher)} in many places.
 * <p>
 * Also it can be used in places where the property to be read is not a JavaBean compliant property like AtomicXXX.get()
 * methods, non public getters or methods having different names (these are places where the aforementioned matcher
 * cannot be used.
 * <p>
 * The benefit of using the mapping matchers can be seen when dealing with collections of objects.
 * <p>
 * Examples:
 * <pre>
 * assertThat(iterableOfAtomicInteger, everyItem(mappedBy(AtomicInteger::get, greaterThanOrEqualTo(21))));
 *
 * assertThat(list, everyItem(mappedBy(Person::getAge, greaterThanOrEqualTo(21))));
 *
 * assertThat(list, hasItem(mappedBy(Person::getName, startsWith("Alice"))));
 *
 * assertThat(list, mapIterable(Person::getName, hasItem("Ana")));
 *
 * assertThat(array, mapArray(Person::getName, hasItem(startsWith("Ana"))));
 * </pre>
 * <p>
 * Another feature is the {@code lambdaAssert()} method which offers a more detailed message on failure.
 * This method can be used as a replacement of {@code assertThat()} method.
 * <p>
 * A code like:
 * <pre>
 * assertThat(p.getName(), equalTo("Brutus));
 * </pre>
 * <p>
 * Can easily be converted to a code that is more useful in case of failure:
 * <pre>
 * lambdaAssert(p::getName, equalTo("Brutus));
 * </pre>
 *
 * @since 0.1
 */
public final class LambdaMatchers {

    private LambdaMatchers() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Creates a {@link Matcher} for an object having a feature with {@code featureName} name.
     * The {@code matcher} argument will be applied on the result of the {@code mapper} function.
     * This method is useful to be used by other utility methods that use functions to convert the input like
     * {@link OptionalMatchers}.
     * <p>
     * <b>This method can be used to easily create feature matchers.</b>
     *
     * @param mapper      The function that transforms the input.
     * @param featureName The name of the <b>feature</b> extracted by the mapper.
     * @param matcher     The {@link Matcher} to be applied on the result of the {@code mapper} function.
     * @param <T>         The type of the input.
     * @param <U>         The type of the result of the {@code mapper} function.
     * @since 0.9
     */
    public static <T, U> Matcher<T> mappedBy(Function<? super T, ? extends U> mapper, String featureName, Matcher<? super U> matcher) {
        return MappedValueMatcher.mappedBy(mapper, featureName, matcher);
    }

    /**
     * Utility method that creates a functional mapper matcher. It receives as input a {@code mapper} and
     * a {@code matcher} that will be applied on the result of the {@code mapper} function.
     * It tries to auto-magically determine the type of the input object and of the {@code mapper} function result.
     * <p>
     * This method is useful ca used to extract properties of objects, or call other functions.
     * It can be used to replace {@link org.hamcrest.Matchers#hasProperty(String, Matcher)}.
     * <p>
     * Examples:
     * <pre>
     * assertThat(iterableOfAtomicInteger, everyItem(mappedBy(AtomicInteger::get, greaterThanOrEqualTo(21))));
     *
     * assertThat(list, everyItem(mappedBy(Person::getAge, greaterThanOrEqualTo(21))));
     *
     * assertThat(list, hasItem(mappedBy(Person::getName, startsWith("Alice"))));
     * </pre>
     *
     * @param mapper  The function that transforms the input.
     * @param matcher The {@link Matcher} to be applied on the result of the {@code mapper} function.
     * @param <T>     The type of the input.
     * @param <U>     The type of the result of the {@code mapper} function.
     * @see #mappedBy(Function, Matcher)
     * @since 0.9
     */
    public static <T, U> Matcher<T> mappedBy(Function<? super T, ? extends U> mapper, Matcher<? super U> matcher) {
        return MappedValueMatcher.mappedBy(mapper, matcher);
    }

    /**
     * Utility method that creates a matcher that converts an iterable of {@code <T>} to an iterable of
     * {@code <U>} allowing us to use an iterable matcher on the result of the mapping function.
     * <p>
     * Example:
     * <pre>
     * assertThat(list, mapIterable(Person::getName, hasItem("Ana")));
     * </pre>
     *
     * @param mapper  The function that transforms every element of the input iterable.
     * @param matcher The matcher to be applied on the resulting iterable.
     * @param <T>     The type of the elements in the input iterable.
     * @param <U>     The type of the result of the {@code mapper} function.
     * @since 0.1
     */
    public static <T, U> Matcher<Iterable<T>> mapIterable(Function<? super T, ? extends U> mapper,
                                                                    Matcher<Iterable<? super U>> matcher) {
        return mappedBy(iterable -> transformIterable(mapper, iterable), matcher);
    }

    private static <T, U> Iterable<U> transformIterable(Function<? super T, ? extends U> mapper, Iterable<? extends T> iterable) {
        ArrayList<U> result = new ArrayList<>();
        for (T element : iterable) {
            result.add(mapper.apply(element));
        }
        return result;
    }

    /**
     * Utility method that creates a matcher that converts an array of {@code <T>} to an iterable of
     * {@code <U>} allowing us to use an iterable matcher on the result of the mapping function.
     * <p>
     * Example:
     * <pre>
     * assertThat(array, mapArray(Person::getName, hasItem(startsWith("Ana"))));
     * </pre>
     *
     * @param mapper  The function that transforms every element of the input array.
     * @param matcher The matcher to be applied on the resulting iterable.
     * @param <T>     The type of the elements in the input array.
     * @param <U>     The type of the result of the {@code mapper} function.
     * @since 0.1
     */
    public static <T, U> Matcher<T[]> mapArray(Function<? super T, ? extends U> mapper, Matcher<Iterable<? super U>> matcher) {
        return mappedBy(array -> transformArray(mapper, array), matcher);
    }

    private static <T, U> Iterable<U> transformArray(Function<? super T, ? extends U> mapper, T[] array) {
        ArrayList<U> result = new ArrayList<>(array.length);
        for (T element : array) {
            result.add(mapper.apply(element));
        }
        return result;
    }

    /**
     * This is an assert function that takes as input a supplier and a matcher for its value.
     * In case of mismatch it offers a more detailed error message than the
     * {@link org.hamcrest.MatcherAssert#assertThat(String, boolean)} alternative.
     * <p>
     * This method can be used as a replacement of {@code assertThat()} method.
     * <p>
     * A code like:
     * <pre>
     * assertThat(p.getName(), equalTo("Brutus));
     * </pre>
     * <p>
     * Can easily be converted to a code that is more useful in case of failure:
     * <pre>
     * lambdaAssert(p::getName, equalTo("Brutus));
     * </pre>
     *
     * @param supplier The supplier for the value.
     * @param matcher  The {@link Matcher} to be applied on the value supplied by the {@code supplier}.
     * @param <T>      The type of the supplied value.
     * @since 0.9
     */
    public static <T> void lambdaAssert(Supplier<T> supplier, Matcher<? super T> matcher) {
        assertThat(supplier, supplierMatcher(supplier, matcher));
    }
}
