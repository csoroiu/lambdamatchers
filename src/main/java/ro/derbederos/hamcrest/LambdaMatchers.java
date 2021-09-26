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

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static ro.derbederos.hamcrest.TypeResolverFeatureMatcherFactory.supplierMatcher;

/**
 * <p>
 * This class provides a set of matchers based on java 8+ functional interfaces (lambdas).
 * </p>
 * <p>
 * They are useful in plenty of places where you are required to make assertions on different properties of objects.
 * </p>
 * <p>
 * It can replace {@link org.hamcrest.Matchers#hasProperty(java.lang.String, org.hamcrest.Matcher)} in many places.
 * </p>
 * <p>
 * Also it can be used in places where the property to be read is not a JavaBean compliant property like AtomicXXX.get()
 * methods, non public getters or methods having different names (these are places where the aforementioned matcher
 * cannot be used.
 * </p>
 * <p>
 * The benefit of using the mapping matchers can be seen when dealing with collections of objects.
 * </p>
 * <p>
 * Examples:
 * </p>
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
 * </p>
 * <p>
 * A code like:
 * </p>
 * <pre>
 * assertThat(p.getName(), equalTo("Brutus"));
 * </pre>
 * <p>
 * Can easily be converted to a code that is more useful in case of failure:
 * </p>
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
     * <p>
     * Creates a {@link Matcher} for an object having a feature with {@code featureName} name.
     * The {@code featureMatcher} argument will be applied on the result of the {@code featureExtractor} function.
     * </p>
     * <p>
     * <b>This method can be used to easily create feature matchers.</b>
     * </p>
     *
     * @param <T>              The type of the input.
     * @param <U>              The type of the result of the {@code featureExtractor} function.
     * @param featureName      The name of the <b>feature</b> extracted by the {@code featureExtractor}.
     * @param featureExtractor The function that transforms the input.
     * @param featureMatcher   The {@link Matcher} to be applied on the result of the {@code featureExtractor} function.
     * @see #hasFeature(Function, Matcher)
     * @since 0.17
     */
    public static <T, U> Matcher<T> hasFeature(String featureName,
                                               Function<? super T, ? extends U> featureExtractor,
                                               Matcher<? super U> featureMatcher) {
        return TypeResolverFeatureMatcherFactory.feature(featureName, featureExtractor, featureMatcher);
    }

    /**
     * <p>
     * Utility method that creates a feature matcher. It receives as input a {@code featureExtractor} and
     * a {@code featureMatcher} that will be applied on the result of the {@code featureExtractor} function.
     * It tries to auto-magically determine the type of the input object and of the {@code featureExtractor} function result.
     * </p>
     * <p>
     * This method is useful ca used to extract properties of objects, or call other functions.
     * It can be used to replace {@link org.hamcrest.Matchers#hasProperty(java.lang.String, org.hamcrest.Matcher)}.
     * </p>
     * <p>
     * Examples:
     * </p>
     * <pre>
     * assertThat(iterableOfAtomicInteger, everyItem(hasFeature(AtomicInteger::get, greaterThanOrEqualTo(21))));
     *
     * assertThat(list, everyItem(hasFeature(Person::getAge, greaterThanOrEqualTo(21))));
     *
     * assertThat(list, hasItem(hasFeature(Person::getName, startsWith("Alice"))));
     * </pre>
     *
     * @param featureExtractor The function that transforms the input.
     * @param featureMatcher   The {@link Matcher} to be applied on the result of the {@code featureExtractor} function.
     * @param <T>              The type of the input.
     * @param <U>              The type of the result of the {@code featureExtractor} function.
     * @see #hasFeature(String, Function, Matcher)
     * @since 0.17
     */
    public static <T, U> Matcher<T> hasFeature(Function<? super T, ? extends U> featureExtractor, Matcher<? super U> featureMatcher) {
        return TypeResolverFeatureMatcherFactory.feature(featureExtractor, featureMatcher);
    }

    /**
     * <p>
     * Utility method that creates a matcher that converts an iterable of {@code <T>} to an iterable of
     * {@code <U>} allowing us to use an iterable matcher on the result of the mapping function.
     * </p>
     * <p>
     * Example:
     * </p>
     * <pre>
     * assertThat(list, featureIterable(Person::getName, hasItem("Ana")));
     * </pre>
     *
     * @param featureExtractor The function that transforms every element of the input iterable.
     * @param iterableMatcher  The matcher to be applied on the resulting iterable.
     * @param <T>              The type of the elements in the input iterable.
     * @param <U>              The type of the result of the {@code featureExtractor} function.
     * @since 0.17
     */
    public static <T, U> Matcher<Iterable<T>> featureIterable(Function<? super T, ? extends U> featureExtractor,
                                                              Matcher<? extends Iterable<? super U>> iterableMatcher) {
        return TypeResolverFeatureMatcherFactory.featureIterable(featureExtractor, iterableMatcher);
    }

    /**
     * Utility method that creates a matcher that converts an array of {@code <T>} to an iterable of
     * {@code <U>} allowing us to use an iterable matcher on the result of the mapping function.
     * <p>
     * Example:
     * </p>
     * <pre>
     * assertThat(array, featureArray(Person::getName, hasItem(startsWith("Ana"))));
     * </pre>
     *
     * @param featureExtractor The function that transforms every element of the input array.
     * @param iterableMatcher  The matcher to be applied on the resulting iterable.
     * @param <T>              The type of the elements in the input array.
     * @param <U>              The type of the result of the {@code featureExtractor} function.
     * @since 0.17
     */
    public static <T, U> Matcher<T[]> featureArray(Function<? super T, ? extends U> featureExtractor,
                                                   Matcher<Iterable<? super U>> iterableMatcher) {
        return TypeResolverFeatureMatcherFactory.featureArray(featureExtractor, iterableMatcher);
    }

    /**
     * <p>
     * This is an assert function that takes as input a supplier and a matcher for its value.
     * In case of mismatch it offers a more detailed error message than the
     * {@link org.hamcrest.MatcherAssert#assertThat(String, boolean)} alternative.
     * </p>
     * <p>
     * This method can be used as a replacement of {@code assertThat()} method.
     * </p>
     * <p>
     * A code like:
     * </p>
     * <pre>
     * assertThat(p.getName(), equalTo("Brutus"));
     * </pre>
     * <p>
     * Can easily be converted to a code that is more useful in case of failure:
     * </p>
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

    /**
     * <p>
     * Utility method that returns a function which returns an {@link Iterable} of items, each of them representing a feature
     * of a supplied object. Each feature is extracted using one of the {@code featureExtractors}.
     * </p>
     * <p>
     * Examples:
     * </p>
     * <pre>
     * assertThat(p, hasFeature(extract(Person::getName, Person::getAge), contains("Alice", 21)))
     *
     * assertThat(p, hasFeature(extract(Person::getName, Person::getAge), hasItems("Alice", 21)))
     * </pre>
     *
     * @param featureExtractors A list of functions representing feature extractors.
     * @param <T>               The type of the supplied object.
     * @since 0.19
     */
    @SafeVarargs
    static <T> Function<T, Iterable<Object>> extract(Function<? super T, ?>... featureExtractors) {
        return actual -> Stream.of(featureExtractors)
                .map(extractor -> extractor.apply(actual))
                .collect(toList());
    }
}
