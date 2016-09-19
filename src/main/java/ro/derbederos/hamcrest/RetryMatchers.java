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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.Matchers.equalTo;
import static ro.derbederos.hamcrest.LambdaMatchers.map;

/**
 * This class provides a set of matchers that retry a certain {@link Matcher} until a duration is reached.
 * Useful for some concurrent tests, where we expect a certain value to be reached in a reasonable amount of time.
 * <p>
 * <i>These matchers are not compatible with streams, if received as an input.</i>
 *
 * @since 0.2
 */
public class RetryMatchers {

    private RetryMatchers() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Returns a {@link Matcher} that checks if the given <code>matcher</code> matches the input, every
     * <code>interval</code>, until <code>duration</code> is reached.
     *
     * @param duration The duration of the retry. Will fail afterwards if <code>matcher</code> fails.
     * @param interval The interval between two consecutive checks.
     * @param timeUnit The {@link TimeUnit} in which <code>duration</code> and <code>interval</code> are represented.
     * @param matcher  The {@link Matcher} to be applied on the input.
     * @param <T>      The type of the input.
     * @since 0.2
     */
    public static <T> Matcher<T> retry(long duration, long interval, TimeUnit timeUnit, Matcher<? super T> matcher) {
        return RetryMatcher.retry(duration, interval, timeUnit, matcher);
    }

    /**
     * Returns a {@link Matcher} that checks if the given <code>matcher</code> matches the input, every
     * <code>50 ms</code>, until <code>duration</code> is reached.
     *
     * @param duration The duration of the retry. Will fail afterwards if <code>matcher</code> fails.
     * @param timeUnit The {@link TimeUnit} in which <code>duration</code> is represented.
     * @param matcher  The {@link Matcher} to be applied on the input.
     * @param <T>      The type of the input.
     * @since 0.2
     */
    public static <T> Matcher<T> retry(long duration, TimeUnit timeUnit, Matcher<? super T> matcher) {
        return RetryMatcher.retry(duration, timeUnit, matcher);
    }

    /**
     * Returns a {@link Matcher} that checks if the given <code>matcher</code> matches the input, every
     * <code>intervalMillis</code>, until <code>durationMillis</code> is reached.
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if <code>matcher</code> fails.
     * @param intervalMillis The interval between two consecutive checks.
     * @param matcher        The {@link Matcher} to be applied on the input.
     * @param <T>            The type of the input.
     * @since 0.2
     */
    public static <T> Matcher<T> retry(long durationMillis, long intervalMillis, Matcher<? super T> matcher) {
        return retry(durationMillis, intervalMillis, MILLISECONDS, matcher);
    }

    /**
     * Returns a {@link Matcher} that checks if the given <code>matcher</code> matches the input, every
     * <code>50 ms</code>, until <code>durationMillis</code> is reached.
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if <code>matcher</code> fails.
     * @param matcher        The {@link Matcher} to be applied on the input.
     * @param <T>            The type of the input.
     * @since 0.2
     */
    public static <T> Matcher<T> retry(long durationMillis, Matcher<? super T> matcher) {
        return retry(durationMillis, MILLISECONDS, matcher);
    }

    /**
     * Returns a {@link Matcher} that checks if the given <code>matcher</code> matches the value of the {@link AtomicInteger}
     * received as input. It retries every <code>50 ms</code>, until <code>durationMillis</code> is reached.
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if <code>matcher</code> fails.
     * @param matcher        The {@link Matcher} to be applied on the value of the {@link AtomicInteger}.
     * @since 0.2
     */
    public static Matcher<AtomicInteger> retryAtomicInteger(long durationMillis, Matcher<Integer> matcher) {
        return retry(durationMillis, MILLISECONDS, map(AtomicInteger::get, matcher));
    }

    /**
     * Returns a {@link Matcher} that checks if the value of the {@link AtomicInteger} is equal to the received
     * <code>value</code>. It retries every <code>50 ms</code>, until <code>durationMillis</code> is reached.
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if value does not match.
     * @since 0.2
     */
    public static Matcher<AtomicInteger> retryAtomicInteger(long durationMillis, int value) {
        return retryAtomicInteger(durationMillis, equalTo(value));
    }

    /**
     * Returns a {@link Matcher} that checks if the given <code>matcher</code> matches the value of the {@link AtomicLong}
     * received as input. It retries every <code>50 ms</code>, until <code>durationMillis</code> is reached.
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if <code>matcher</code> fails.
     * @param matcher        The {@link Matcher} to be applied on the value of the {@link AtomicLong}.
     * @since 0.2
     */
    public static Matcher<AtomicLong> retryAtomicLong(long durationMillis, Matcher<Long> matcher) {
        return retry(durationMillis, MILLISECONDS, map(AtomicLong::get, matcher));
    }

    /**
     * Returns a {@link Matcher} that checks if the value of the {@link AtomicLong} is equal to the received
     * <code>value</code>. It retries every <code>50 ms</code>, until <code>durationMillis</code> is reached.
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if value does not match.
     * @since 0.2
     */
    public static Matcher<AtomicLong> retryAtomicLong(long durationMillis, long value) {
        return retryAtomicLong(durationMillis, equalTo(value));
    }

    /**
     * Returns a {@link Matcher} that checks if the given <code>matcher</code> matches the value of the {@link AtomicBoolean}
     * received as input. It retries every <code>50 ms</code>, until <code>durationMillis</code> is reached.
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if <code>matcher</code> fails.
     * @param matcher        The {@link Matcher} to be applied on the value of the {@link AtomicBoolean}.
     * @since 0.2
     */
    public static Matcher<AtomicBoolean> retryAtomicBoolean(long durationMillis, Matcher<Boolean> matcher) {
        return retry(durationMillis, MILLISECONDS, map(AtomicBoolean::get, matcher));
    }

    /**
     * Returns a {@link Matcher} that checks if the value of the {@link AtomicBoolean} is equal to the received
     * <code>value</code>. It retries every <code>50 ms</code>, until <code>durationMillis</code> is reached.
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if value does not match.
     * @since 0.2
     */
    public static Matcher<AtomicBoolean> retryAtomicBoolean(long durationMillis, boolean value) {
        return retryAtomicBoolean(durationMillis, equalTo(value));
    }

    /**
     * Returns a {@link Matcher} that checks if the given <code>matcher</code> matches the value of the {@link AtomicReference}
     * received as input. It retries every <code>50 ms</code>, until <code>durationMillis</code> is reached.
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if <code>matcher</code> fails.
     * @param matcher        The {@link Matcher} to be applied on the value of the {@link AtomicReference}.
     * @param <V>            The type of the {@link AtomicReference} value.
     * @since 0.2
     */
    public static <V> Matcher<AtomicReference<V>> retryAtomicReference(long durationMillis, Matcher<? super V> matcher) {
        return retry(durationMillis, MILLISECONDS, map(AtomicReference::get, matcher));
    }

    /**
     * Returns a {@link Matcher} that checks if the value of the {@link AtomicReference} is equal to the received
     * <code>value</code>. It retries every <code>50 ms</code>, until <code>durationMillis</code> is reached.
     *
     * @param durationMillis The duration of the retry. Will fail afterwards if value does not match.
     * @param <V>            The type of the {@link AtomicReference} value.
     * @since 0.2
     */
    public static <V> Matcher<AtomicReference<V>> retryAtomicReference(long durationMillis, V value) {
        return retryAtomicReference(durationMillis, equalTo(value));
    }
}
