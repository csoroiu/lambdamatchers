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

import java8.util.function.Function;
import java8.util.function.Supplier;
import org.hamcrest.Matcher;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static ro.derbederos.hamcrest.LambdaMatchers.mappedBy;
import static ro.derbederos.hamcrest.MappedValueMatcher.supplierMatcher;

/**
 * This class provides a set of matchers that retry a certain {@link Matcher} until a duration is reached.
 * Useful for some concurrent tests, where we expect a certain value to be reached in a reasonable amount of time.
 * <p>
 * Examples:
 * <pre>
 * assertThat(mutableObject, retry(500, a -&gt; a.getValue(), equalTo(7)));
 *
 * assertThat(bean, retry(300, hasProperty("value", equalTo(9))));
 *
 * assertThat(atomicReferenceSpell, retryAtomicReference(500, powerfulThan("Expecto Patronum")));
 *
 * assertThat(atomicInteger, retryAtomicInteger(300, 9));
 *
 * assertThat(atomicLong, retryAtomicLong(300, greaterThan(10L)));
 * </pre>
 * <p>
 * <i>These matchers are not compatible with streams, if received directly as an input.
 * When dealing with streams one should work with some sort of stream provider in order to use the retry matchers.</i>
 *
 * @since 0.2
 */
public final class RetryMatchers {

    private RetryMatchers() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the input, every
     * {@code interval}, until {@code duration} is reached.
     *
     * @param duration The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param interval The interval between two consecutive checks.
     * @param timeUnit The {@link TimeUnit} in which {@code duration} and {@code interval} are represented.
     * @param matcher  The {@link Matcher} to be applied on the input.
     * @param <T>      The type of the input.
     * @since 0.2
     */
    public static <T> Matcher<T> retry(long duration, long interval, TimeUnit timeUnit, Matcher<? super T> matcher) {
        return RetryMatcher.retry(duration, interval, timeUnit, matcher);
    }

    /**
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the input, every
     * {@code 50 ms}, until {@code duration} is reached.
     *
     * @param duration The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param timeUnit The {@link TimeUnit} in which {@code duration} is represented.
     * @param matcher  The {@link Matcher} to be applied on the input.
     * @param <T>      The type of the input.
     * @since 0.2
     */
    public static <T> Matcher<T> retry(long duration, TimeUnit timeUnit, Matcher<? super T> matcher) {
        return RetryMatcher.retry(duration, timeUnit, matcher);
    }

    /**
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the input, every
     * {@code intervalMillis}, until {@code durationMillis} is reached.
     * <p>
     * Example:
     * <pre>
     * assertThat(bean, retry(500, 25, hasProperty("value", equalTo(7))));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param intervalMillis The interval between two consecutive checks.
     * @param matcher        The {@link Matcher} to be applied on the input.
     * @param <T>            The type of the input.
     * @since 0.2
     */
    public static <T> Matcher<T> retry(long durationMillis, long intervalMillis, Matcher<? super T> matcher) {
        return retry(durationMillis, intervalMillis, MILLISECONDS, matcher);
    }

    /**
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the input, every
     * {@code 50 ms}, until {@code durationMillis} is reached.
     * <p>
     * Example:
     * <pre>
     * assertThat(bean, retry(300, hasProperty("value", equalTo(9))));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param matcher        The {@link Matcher} to be applied on the input.
     * @param <T>            The type of the input.
     * @since 0.2
     */
    public static <T> Matcher<T> retry(long durationMillis, Matcher<? super T> matcher) {
        return retry(durationMillis, MILLISECONDS, matcher);
    }

    /**
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the input, every
     * {@code 50 ms}, until {@code durationMillis} is reached. This method receives a {@link Function} as
     * input and builds a mapping matcher out of the {@code mapper} and the received {@code matcher}.
     * <p>
     * It is a shortcut for:
     * <pre>
     * retry(durationMillis, mappedBy(mapper, matcher));
     * </pre>
     * <p>
     * Example:
     * <pre>
     * assertThat(bean, retry(500, b -&gt; b.getValue(), equalTo(7)));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param mapper         The function that transforms the input.
     * @param matcher        The {@link Matcher} to be applied on the result of the {@code mapper} function.
     * @param <T>            The type of the input.
     * @param <U>            The type of the result of the {@code mapper} function.
     * @since 0.3
     */
    public static <T, U> Matcher<T> retry(long durationMillis, Function<? super T, ? extends U> mapper, Matcher<? super U> matcher) {
        return retry(durationMillis, mappedBy(mapper, matcher));
    }

    /**
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the value of the {@link AtomicInteger}
     * received as input. It retries every {@code 50 ms}, until {@code durationMillis} is reached.
     * <p>
     * Example:
     * <pre>
     * assertThat(atomicInteger, retryAtomicInteger(300, greaterThan(10)));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param matcher        The {@link Matcher} to be applied on the value of the {@link AtomicInteger}.
     * @since 0.2
     */
    public static Matcher<AtomicInteger> retryAtomicInteger(long durationMillis, Matcher<Integer> matcher) {
        return retry(durationMillis, AtomicInteger::intValue, matcher);
    }

    /**
     * Creates a {@link Matcher} that checks if the value of the {@link AtomicInteger} is equal to the received
     * {@code value}. It retries every {@code 50 ms}, until {@code durationMillis} is reached.
     * <p>
     * Example:
     * <pre>
     * assertThat(atomicInteger, retryAtomicInteger(300, 9));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if value does not match.
     * @param value          The value to match against.
     * @since 0.2
     */
    public static Matcher<AtomicInteger> retryAtomicInteger(long durationMillis, int value) {
        return retryAtomicInteger(durationMillis, equalTo(value));
    }

    /**
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the value of the {@link AtomicLong}
     * received as input. It retries every {@code 50 ms}, until {@code durationMillis} is reached.
     * <p>
     * Example:
     * <pre>
     * assertThat(atomicLong, retryAtomicLong(300, greaterThan(10)));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param matcher        The {@link Matcher} to be applied on the value of the {@link AtomicLong}.
     * @since 0.2
     */
    public static Matcher<AtomicLong> retryAtomicLong(long durationMillis, Matcher<Long> matcher) {
        return retry(durationMillis, AtomicLong::longValue, matcher);
    }

    /**
     * Creates a {@link Matcher} that checks if the value of the {@link AtomicLong} is equal to the received
     * {@code value}. It retries every {@code 50 ms}, until {@code durationMillis} is reached.
     * <p>
     * Example:
     * <pre>
     * assertThat(atomicLong, retryAtomicLong(300, 9));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if value does not match.
     * @param value          The value to match against.
     * @since 0.2
     */
    public static Matcher<AtomicLong> retryAtomicLong(long durationMillis, long value) {
        return retryAtomicLong(durationMillis, equalTo(value));
    }

    /**
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the value of the {@link LongAccumulator}
     * received as input. It retries every {@code 50 ms}, until {@code durationMillis} is reached.
     * <p>
     * Example:
     * <pre>
     * assertThat(accumulator, retryLongAccumulator(300, greaterThan(10L)));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param matcher        The {@link Matcher} to be applied on the value of the {@link LongAccumulator}.
     * @since 0.11
     */
    @SuppressWarnings("Since15")
    @Java8API
    public static Matcher<LongAccumulator> retryLongAccumulator(long durationMillis, Matcher<Long> matcher) {
        return retry(durationMillis, LongAccumulator::longValue, matcher);
    }

    /**
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the value of the {@link LongAdder}
     * received as input. It retries every {@code 50 ms}, until {@code durationMillis} is reached.
     * <p>
     * Example:
     * <pre>
     * assertThat(adder, retryLongAdder(300, greaterThan(10L)));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param matcher        The {@link Matcher} to be applied on the value of the {@link LongAdder}.
     * @since 0.11
     */
    @SuppressWarnings("Since15")
    @Java8API
    public static Matcher<LongAdder> retryLongAdder(long durationMillis, Matcher<Long> matcher) {
        return retry(durationMillis, LongAdder::longValue, matcher);
    }

    /**
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the value of the {@link DoubleAccumulator}
     * received as input. It retries every {@code 50 ms}, until {@code durationMillis} is reached.
     * <p>
     * Example:
     * <pre>
     * assertThat(accumulator, retryDoubleAccumulator(300, greaterThan(10.0)));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param matcher        The {@link Matcher} to be applied on the value of the {@link DoubleAccumulator}.
     * @since 0.11
     */
    @SuppressWarnings("Since15")
    @Java8API
    public static Matcher<DoubleAccumulator> retryDoubleAccumulator(long durationMillis, Matcher<Double> matcher) {
        return retry(durationMillis, DoubleAccumulator::doubleValue, matcher);
    }

    /**
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the value of the {@link DoubleAdder}
     * received as input. It retries every {@code 50 ms}, until {@code durationMillis} is reached.
     * <p>
     * Example:
     * <pre>
     * assertThat(adder, retryDoubleAdder(300, greaterThan(10.0)));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param matcher        The {@link Matcher} to be applied on the value of the {@link DoubleAdder}.
     * @since 0.11
     */
    @SuppressWarnings("Since15")
    @Java8API
    public static Matcher<DoubleAdder> retryDoubleAdder(long durationMillis, Matcher<Double> matcher) {
        return retry(durationMillis, DoubleAdder::doubleValue, matcher);
    }

    /**
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the value of the {@link AtomicBoolean}
     * received as input. It retries every {@code 50 ms}, until {@code durationMillis} is reached.
     * <p>
     * Example:
     * <pre>
     * assertThat(atomicBoolean, retryAtomicBoolean(300, equalTo(false)));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param matcher        The {@link Matcher} to be applied on the value of the {@link AtomicBoolean}.
     * @since 0.2
     */
    public static Matcher<AtomicBoolean> retryAtomicBoolean(long durationMillis, Matcher<Boolean> matcher) {
        return retry(durationMillis, AtomicBoolean::get, matcher);
    }

    /**
     * Creates a {@link Matcher} that checks if the value of the {@link AtomicBoolean} is equal to the received
     * {@code value}. It retries every {@code 50 ms}, until {@code durationMillis} is reached.
     * <p>
     * Example:
     * <pre>
     * assertThat(atomicBoolean, retryAtomicBoolean(300, true));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if value does not match.
     * @param value          The value to match against.
     * @since 0.2
     */
    public static Matcher<AtomicBoolean> retryAtomicBoolean(long durationMillis, boolean value) {
        return retryAtomicBoolean(durationMillis, equalTo(value));
    }

    /**
     * Creates a {@link Matcher} that checks if the given {@code matcher} matches the value of the {@link AtomicReference}
     * received as input. It retries every {@code 50 ms}, until {@code durationMillis} is reached.
     * <p>
     * Example:
     * <pre>
     * assertThat(atomicReferenceSpell, retryAtomicReference(500, powerfulThan("Expecto Patronum")));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param matcher        The {@link Matcher} to be applied on the value of the {@link AtomicReference}.
     * @param <V>            The type of the {@link AtomicReference} value.
     * @since 0.2
     */
    public static <V> Matcher<AtomicReference<V>> retryAtomicReference(long durationMillis, Matcher<? super V> matcher) {
        return retry(durationMillis, AtomicReference::get, matcher);
    }

    /**
     * Creates a {@link Matcher} that checks if the value of the {@link AtomicReference} is equal to the received
     * {@code value}. It retries every {@code 50 ms}, until {@code durationMillis} is reached.
     * <p>
     * Example:
     * <pre>
     * assertThat(atomicReferenceSpell, retryAtomicReference(500, "Expecto Patronum"));
     * </pre>
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if value does not match.
     * @param value          The value to match against.
     * @param <V>            The type of the {@link AtomicReference} value.
     * @since 0.2
     */
    public static <V> Matcher<AtomicReference<V>> retryAtomicReference(long durationMillis, V value) {
        return retryAtomicReference(durationMillis, equalTo(value));
    }

    /**
     * This is an assert function that takes as input a supplier and a matcher for its value.
     * It is a fancy version of {@link LambdaMatchers#lambdaAssert(Supplier, Matcher)} as it creates
     * a retrying matcher in the back and retries to match the value against the received matcher
     * several times.
     * <p>
     * Example:
     * <pre>
     * lambdaAssert(p::getName, 500, equalTo("Brutus"));
     * </pre>
     *
     * @param supplier       The supplier for the value.
     * @param durationMillis The duration of the retry. Will fail afterwards if {@code matcher} fails.
     * @param matcher        The {@link Matcher} to be applied on the value supplied by the {@code supplier}.
     * @param <T>            The type of the supplied value.
     * @since 0.9
     */
    public static <T> void lambdaAssert(Supplier<T> supplier, long durationMillis, Matcher<? super T> matcher) {
        assertThat(supplier, retrySupplier(durationMillis, supplier, matcher));
    }

    static <T> Matcher<Supplier<T>> retrySupplier(long durationMillis, Supplier<T> supplier, Matcher<? super T> matcher) {
        return retry(durationMillis, supplierMatcher(supplier, matcher));
    }
}
