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

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static ro.derbederos.hamcrest.TypeResolverFeatureMatcherFactory.supplierMatcher;

/**
 * <p>
 * This class provides a set of matchers that retry a certain {@link Matcher} until a timeout is reached.
 * Useful for some concurrent tests, where we expect a certain value to be reached in a reasonable amount of time.
 * </p>
 * <p>
 * Examples:
 * </p>
 * <pre>
 * assertThat(mutableObject, retry(500, MutableObjectClass::getValue, equalTo(7)));
 *
 * assertThat(bean, retry(300, hasProperty("value", equalTo(9))));
 *
 * assertThat(atomicReferenceSpell, retry(500, AtomicReference::get, powerfulThan("Expecto Patronum")));
 *
 * assertThat(atomicInteger, retry(300, AtomicInteger::intValue, equalTo(9)));
 *
 * assertThat(atomicLong, retry(300, AtomicLong::longValue, greaterThan(10L)));
 * </pre>
 * <p>
 * <i>These matchers are not compatible with streams, if received directly as an input.
 * When dealing with streams one should work with some sort of stream provider in order to use the retry matchers.</i>
 * </p>
 *
 * @since 0.2
 */
public final class RetryMatchers {

    private RetryMatchers() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * <p>
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the input, every
     * {@code interval}, until {@code timeout} is reached.
     * </p>
     *
     * @param timeout  The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param interval The interval between two consecutive checks.
     * @param timeUnit The {@link TimeUnit} in which {@code timeout} and {@code interval} are represented.
     * @param matcher  The {@link Matcher} to be applied on the input.
     * @param <T>      The type of the input.
     * @since 0.2
     */
    public static <T> Matcher<T> retry(long timeout,
                                       long interval,
                                       TimeUnit timeUnit,
                                       Matcher<? super T> matcher) {
        return RetryMatcher.retry(timeout, interval, timeUnit, matcher);
    }

    /**
     * <p>
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the input, every
     * {@code 50 ms}, until {@code timeout} is reached.
     * </p>
     *
     * @param timeout  The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param timeUnit The {@link TimeUnit} in which {@code timeout} is represented.
     * @param matcher  The {@link Matcher} to be applied on the input.
     * @param <T>      The type of the input.
     * @since 0.2
     */
    public static <T> Matcher<T> retry(long timeout,
                                       TimeUnit timeUnit,
                                       Matcher<? super T> matcher) {
        return RetryMatcher.retry(timeout, timeUnit, matcher);
    }

    /**
     * <p>
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the input, every
     * {@code intervalMillis}, until {@code timeoutMillis} is reached.
     * </p>
     * <p>
     * Example:
     * </p>
     * <pre>
     * assertThat(bean, retry(500, 25, hasProperty("value", equalTo(7))));
     * </pre>
     *
     * @param timeoutMillis  The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param intervalMillis The interval between two consecutive checks.
     * @param matcher        The {@link Matcher} to be applied on the input.
     * @param <T>            The type of the input.
     * @since 0.2
     */
    public static <T> Matcher<T> retry(long timeoutMillis,
                                       long intervalMillis,
                                       Matcher<? super T> matcher) {
        return retry(timeoutMillis, intervalMillis, MILLISECONDS, matcher);
    }

    /**
     * <p>
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the input, every
     * {@code 50 ms}, until {@code timeoutMillis} is reached.
     * </p>
     * <p>
     * Example:
     * </p>
     * <pre>
     * assertThat(bean, retry(300, hasProperty("value", equalTo(9))));
     * </pre>
     *
     * @param timeoutMillis The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param matcher       The {@link Matcher} to be applied on the input.
     * @param <T>           The type of the input.
     * @since 0.2
     */
    public static <T> Matcher<T> retry(long timeoutMillis,
                                       Matcher<? super T> matcher) {
        return retry(timeoutMillis, MILLISECONDS, matcher);
    }

    /**
     * <p>
     * Creates a {@link Matcher} that checks if the given {@code featureMatcher} matches the input, every
     * {@code 50 ms}, until {@code timeoutMillis} is reached. This method receives a {@link Function} as
     * input and builds a mapping featureMatcher out of the {@code featureExtractor} and the received {@code featureMatcher}.
     * </p>
     * <p>
     * It is a shortcut for:
     * </p>
     * <pre>
     * retry(timeoutMillis, hasFeature(featureExtractor, featureMatcher));
     * </pre>
     * <p>
     * Example:
     * </p>
     * <pre>
     * assertThat(bean, retry(500, BeanClass::getValue, equalTo(7)));
     * </pre>
     *
     * @param timeoutMillis    The duration of the retry. Will fail afterwards if {@code featureMatcher} fails.
     * @param featureExtractor The function that transforms the input.
     * @param featureMatcher   The {@link Matcher} to be applied on the result of the {@code featureExtractor} function.
     * @param <T>              The type of the input.
     * @param <U>              The type of the result of the {@code featureExtractor} function.
     * @since 0.3
     */
    public static <T, U> Matcher<T> retry(long timeoutMillis,
                                          Function<? super T, ? extends U> featureExtractor,
                                          Matcher<? super U> featureMatcher) {
        return retry(timeoutMillis, TypeResolverFeatureMatcherFactory.feature(featureExtractor, featureMatcher));
    }

    /**
     * <p>
     * This is an assert function that takes as input a supplier and a matcher for its value.
     * It is a fancy version of {@link LambdaMatchers#assertFeature(Supplier, Matcher)} as it creates
     * a retrying matcher in the back and retries to match the value against the received matcher
     * several times.
     * </p>
     * <p>
     * Example:
     * </p>
     * <pre>
     * assertFeature(p::getName, 500, equalTo("Brutus"));
     * </pre>
     *
     * @param supplier      The supplier for the value.
     * @param timeoutMillis The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param matcher       The {@link Matcher} to be applied on the value supplied by the {@code supplier}.
     * @param <T>           The type of the supplied value.
     * @since 0.17
     */
    public static <T> void assertFeature(Supplier<T> supplier, long timeoutMillis, Matcher<? super T> matcher) {
        assertThat(supplier, retrySupplier(timeoutMillis, supplier, matcher));
    }

    static <T> Matcher<Supplier<T>> retrySupplier(long timeoutMillis, Supplier<T> supplier, Matcher<? super T> matcher) {
        return retry(timeoutMillis, supplierMatcher(supplier, matcher));
    }
}
