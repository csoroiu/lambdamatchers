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
import scala.Function1;
import scala.collection.Iterable;
import scala.jdk.javaapi.CollectionConverters;

import static scala.jdk.javaapi.CollectionConverters.asJava;

/**
 * Class description.
 */
public class LambdaMatchersForScala {

    private LambdaMatchersForScala() {
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
     * @see #hasFeature(Function1, Matcher)
     * @since 0.19
     */
    public static <T, U> Matcher<T> hasFeature(String featureName,
                                               Function1<? super T, ? extends U> featureExtractor,
                                               Matcher<? super U> featureMatcher) {
        return TypeResolverFeatureMatcherFactoryForScala.feature(featureName, featureExtractor, featureMatcher);
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
     * @see #hasFeature(String, Function1, Matcher)
     * @since 0.19
     */
    public static <T, U> Matcher<T> hasFeature(Function1<? super T, ? extends U> featureExtractor, Matcher<? super U> featureMatcher) {
        return TypeResolverFeatureMatcherFactoryForScala.feature(featureExtractor, featureMatcher);
    }

    /**
     * <p>
     * Utility method that creates a matcher that converts an {@link Iterable} of {@code <T>} to an {@link java.lang.Iterable} of
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
     * @since 0.19
     */
    public static <T, U> Matcher<Iterable<T>> featureIterable(Function1<? super T, ? extends U> featureExtractor,
                                                              Matcher<java.lang.Iterable<? super U>> iterableMatcher) {

        Function1<Iterable<T>, java.lang.Iterable<? super U>> featureExtractor1;
        //noinspection unchecked
        featureExtractor1 = iterable -> (java.lang.Iterable<? super U>) asJava(iterable.map((Function1<T, ? extends U>) featureExtractor));
        return hasFeature(featureExtractor1, iterableMatcher);
    }

    /**
     * <p>
     * Utility method that creates a matcher that converts a scala {@link Iterable} of {@code <T>} to a java {@link java.lang.Iterable} of
     * {@code <U>} allowing us to use an iterable matcher on a scala {@link Iterable}.
     * </p>
     * <p>
     * Example:
     * </p>
     * <pre>
     * assertThat(list, featureIterable(Person::getName, hasItem("Ana")));
     * </pre>
     *
     * @param iterableMatcher The matcher to be applied on the resulting iterable.
     * @param <T>             The type of the elements in the input iterable.
     * @since 0.19
     */
    public static <T> Matcher<Iterable<T>> asJavaIterableMatcher(Matcher<java.lang.Iterable<? super T>> iterableMatcher) {
        return hasFeature(CollectionConverters::asJava, iterableMatcher);
    }
}
