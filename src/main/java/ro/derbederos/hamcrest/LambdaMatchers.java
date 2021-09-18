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

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static ro.derbederos.hamcrest.TypeResolverFeatureMatcherFactory.supplierMatcher;

/**
 * This class provides a set of mapping matchers based on java 8+ functional interfaces (lambdas).
 * <p>
 * They are useful in plenty of places where you are required to make assertions on different properties of objects.
 * <p>
 * It can replace {@link org.hamcrest.Matchers#hasProperty(java.lang.String, org.hamcrest.Matcher)} in many places.
 * <p>
 * Also it can be used in places where the property to be read is not a JavaBean compliant property like AtomicXXX.get()
 * methods, non public getters or methods having different names (these are places where the aforementioned matcher
 * cannot be used.
 * <p>
 * The benefit of using the mapping matchers can be seen when dealing with collections of objects.
 * <p>
 * Examples:
 * <pre>
 * assertThat(iterableOfAtomicInteger, everyItem(hasFeature(AtomicInteger::get, greaterThanOrEqualTo(21))));
 *
 * assertThat(list, everyItem(hasFeature(Person::getAge, greaterThanOrEqualTo(21))));
 *
 * assertThat(list, hasItem(hasFeature(Person::getName, startsWith("Alice"))));
 *
 * assertThat(list, featureIterable(Person::getName, hasItem("Ana")));
 *
 * assertThat(array, featureArray(Person::getName, hasItem(startsWith("Ana"))));
 * </pre>
 * <p>
 * Another feature is the {@code assertFeature()} method which offers a more detailed message on failure.
 * This method can be used as a replacement of {@code assertThat()} method.
 * <p>
 * A code like:
 * <pre>
 * assertThat(p.getName(), equalTo("Brutus"));
 * </pre>
 * <p>
 * Can easily be converted to a code that is more useful in case of failure:
 * <pre>
 * assertFeature(p::getName, equalTo("Brutus"));
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
     * The {@code featureMatcher} argument will be applied on the result of the {@code featureFunction} function.
     * <p>
     * <b>This method can be used to easily create feature matchers.</b>
     *
     * @param <T>             The type of the input.
     * @param <U>             The type of the result of the {@code featureFunction} function.
     * @param featureName     The name of the <b>feature</b> extracted by the {@code featureFunction}.
     * @param featureFunction The function that transforms the input.
     * @param featureMatcher  The {@link Matcher} to be applied on the result of the {@code featureFunction} function.
     * @since 0.17
     */
    public static <T, U> Matcher<T> hasFeature(String featureName,
                                               Function<? super T, ? extends U> featureFunction,
                                               Matcher<? super U> featureMatcher) {
        return TypeResolverFeatureMatcherFactory.hasFeature(featureName, featureFunction, featureMatcher);
    }

    /**
     * Utility method that creates a feature matcher. It receives as input a {@code featureFunction} and
     * a {@code featureMatcher} that will be applied on the result of the {@code featureFunction} function.
     * It tries to auto-magically determine the type of the input object and of the {@code featureFunction} function result.
     * <p>
     * This method is useful ca used to extract properties of objects, or call other functions.
     * It can be used to replace {@link org.hamcrest.Matchers#hasProperty(java.lang.String, org.hamcrest.Matcher)}.
     * <p>
     * Examples:
     * <pre>
     * assertThat(iterableOfAtomicInteger, everyItem(hasFeature(AtomicInteger::get, greaterThanOrEqualTo(21))));
     *
     * assertThat(list, everyItem(hasFeature(Person::getAge, greaterThanOrEqualTo(21))));
     *
     * assertThat(list, hasItem(hasFeature(Person::getName, startsWith("Alice"))));
     * </pre>
     *
     * @param featureFunction The function that transforms the input.
     * @param featureMatcher  The {@link Matcher} to be applied on the result of the {@code featureFunction} function.
     * @param <T>             The type of the input.
     * @param <U>             The type of the result of the {@code featureFunction} function.
     * @see #hasFeature(Function, Matcher)
     * @since 0.17
     */
    public static <T, U> Matcher<T> hasFeature(Function<? super T, ? extends U> featureFunction, Matcher<? super U> featureMatcher) {
        return TypeResolverFeatureMatcherFactory.hasFeature(featureFunction, featureMatcher);
    }

    /**
     * Utility method that creates a matcher that converts an iterable of {@code <T>} to an iterable of
     * {@code <U>} allowing us to use an iterable matcher on the result of the mapping function.
     * <p>
     * Example:
     * <pre>
     * assertThat(list, featureIterable(Person::getName, hasItem("Ana")));
     * </pre>
     *
     * @param featureFunction The function that transforms every element of the input iterable.
     * @param iterableMatcher The matcher to be applied on the resulting iterable.
     * @param <T>             The type of the elements in the input iterable.
     * @param <U>             The type of the result of the {@code featureFunction} function.
     * @since 0.17
     */
    public static <T, U> Matcher<Iterable<T>> featureIterable(Function<? super T, ? extends U> featureFunction,
                                                              Matcher<Iterable<? super U>> iterableMatcher) {
        return hasFeature(iterable -> transformIterable(featureFunction, iterable), iterableMatcher);
    }

    private static <T, U> Iterable<U> transformIterable(Function<? super T, ? extends U> function, Iterable<? extends T> iterable) {
        ArrayList<U> result = new ArrayList<>();
        for (T element : iterable) {
            result.add(function.apply(element));
        }
        return result;
    }

    /**
     * Utility method that creates a matcher that converts an array of {@code <T>} to an iterable of
     * {@code <U>} allowing us to use an iterable matcher on the result of the mapping function.
     * <p>
     * Example:
     * <pre>
     * assertThat(array, featureArray(Person::getName, hasItem(startsWith("Ana"))));
     * </pre>
     *
     * @param featureFunction The function that transforms every element of the input array.
     * @param iterableMatcher The matcher to be applied on the resulting iterable.
     * @param <T>             The type of the elements in the input array.
     * @param <U>             The type of the result of the {@code featureFunction} function.
     * @since 0.17
     */
    public static <T, U> Matcher<T[]> featureArray(Function<? super T, ? extends U> featureFunction,
                                                   Matcher<Iterable<? super U>> iterableMatcher) {
        return hasFeature(array -> transformArray(featureFunction, array), iterableMatcher);
    }

    private static <T, U> Iterable<U> transformArray(Function<? super T, ? extends U> function, T[] array) {
        ArrayList<U> result = new ArrayList<>(array.length);
        for (T element : array) {
            result.add(function.apply(element));
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
     * assertThat(p.getName(), equalTo("Brutus"));
     * </pre>
     * <p>
     * Can easily be converted to a code that is more useful in case of failure:
     * <pre>
     * assertFeature(p::getName, equalTo("Brutus"));
     * </pre>
     *
     * @param supplier The supplier for the value.
     * @param matcher  The {@link Matcher} to be applied on the value supplied by the {@code supplier}.
     * @param <T>      The type of the supplied value.
     * @since 0.9
     */
    public static <T> void assertFeature(Supplier<T> supplier, Matcher<? super T> matcher) {
        assertThat(supplier, supplierMatcher(supplier, matcher));
    }
}
