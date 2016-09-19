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

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Function;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static ro.derbederos.hamcrest.LambdaMatchers.map;

/**
 * This class provides a set of matchers for {@link Optional}, {@link OptionalInt},  {@link OptionalLong} and
 * {@link OptionalDouble} objects. They are based on the {@link LambdaMatchers#map(Function, String, String, Matcher)}
 * method.
 *
 * @since 0.1
 */
public class OptionalMatchers {

    private OptionalMatchers() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link Optional} has a value.
     *
     * @since 0.1
     */
    public static Matcher<Optional<?>> optionalIsPresent() {
        return map(Optional::isPresent, "Optional.isPresent", "", equalTo(true));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link Optional} is empty.
     *
     * @since 0.1
     */
    public static Matcher<Optional<?>> optionalIsEmpty() {
        return map(Optional::isPresent, "Optional.isPresent", "", equalTo(false));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link Optional}'s content matches the given
     * <code>matcher</code> argument.
     *
     * @param matcher the {@link Matcher} to apply to {@link Optional}'s content.
     * @param <T>     The type of the {@link Optional}'s content.
     * @since 0.1
     */
    public static <T> Matcher<Optional<T>> optionalHasValue(Matcher<? super T> matcher) {
        return allOf(optionalIsPresent(), map(Optional::get, "Optional item:", "", matcher));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link Optional}'s content is equal to the
     * <code>value</code> argument.
     *
     * @param value The expected content of the {@link Optional}.
     * @param <T>   The type of the {@link Optional}'s content.
     * @since 0.1
     */
    public static <T> Matcher<Optional<T>> optionalHasValue(T value) {
        return optionalHasValue(equalTo(value));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link OptionalInt} has a value.
     *
     * @since 0.1
     */
    public static Matcher<OptionalInt> optionalIntIsPresent() {
        return map(OptionalInt::isPresent, "OptionalInt.isPresent", "", equalTo(true));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link OptionalInt} is empty.
     *
     * @since 0.1
     */
    public static Matcher<OptionalInt> optionalIntIsEmpty() {
        return map(OptionalInt::isPresent, "OptionalInt.isPresent", "", equalTo(false));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link OptionalInt}'s content matches the given
     * <code>matcher</code> argument.
     *
     * @param matcher the {@link Matcher} to apply to {@link OptionalInt}'s content.
     * @since 0.1
     */
    public static Matcher<OptionalInt> optionalIntHasValue(Matcher<Integer> matcher) {
        return allOf(optionalIntIsPresent(), map(OptionalInt::getAsInt, "OptionalInt item:", "", matcher));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link OptionalInt}'s content is equal to the
     * <code>value</code> argument.
     *
     * @param value The expected content of the {@link OptionalInt}.
     * @since 0.1
     */
    public static Matcher<OptionalInt> optionalIntHasValue(int value) {
        return optionalIntHasValue(equalTo(value));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link OptionalLong} has a value.
     *
     * @since 0.1
     */
    public static Matcher<OptionalLong> optionalLongIsPresent() {
        return map(OptionalLong::isPresent, "OptionalLong.isPresent", "", equalTo(true));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link OptionalLong} is empty.
     *
     * @since 0.1
     */
    public static Matcher<OptionalLong> optionalLongIsEmpty() {
        return map(OptionalLong::isPresent, "OptionalLong.isPresent", "", equalTo(false));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link OptionalLong}'s content matches the given
     * <code>matcher</code> argument.
     *
     * @param matcher the {@link Matcher} to apply to {@link OptionalLong}'s content.
     * @since 0.1
     */
    public static Matcher<OptionalLong> optionalLongHasValue(Matcher<Long> matcher) {
        return allOf(optionalLongIsPresent(), map(OptionalLong::getAsLong, "OptionalLong item:", "", matcher));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link OptionalLong}'s content is equal to the
     * <code>value</code> argument.
     *
     * @param value The expected content of the {@link OptionalLong}.
     * @since 0.1
     */
    public static Matcher<OptionalLong> optionalLongHasValue(long value) {
        return optionalLongHasValue(equalTo(value));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link OptionalDouble} has a value.
     *
     * @since 0.1
     */
    public static Matcher<OptionalDouble> optionalDoubleIsPresent() {
        return map(OptionalDouble::isPresent, "OptionalDouble.isPresent", "", equalTo(true));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link OptionalDouble} is empty.
     *
     * @since 0.1
     */
    public static Matcher<OptionalDouble> optionalDoubleIsEmpty() {
        return map(OptionalDouble::isPresent, "OptionalDouble.isPresent", "", equalTo(false));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link OptionalDouble}'s content matches the given <code>matcher</code> argument.
     *
     * @param matcher the {@link Matcher} to apply to {@link OptionalDouble}'s content.
     * @since 0.1
     */
    public static Matcher<OptionalDouble> optionalDoubleHasValue(Matcher<Double> matcher) {
        return allOf(optionalDoubleIsPresent(), map(OptionalDouble::getAsDouble, "OptionalDouble item: ", "", matcher));
    }

    /**
     * Returns a {@link Matcher} that checks if the given {@link OptionalDouble}'s content is equal to the <code>value</code> argument.
     *
     * @param value The expected content of the {@link OptionalDouble}.
     * @since 0.1
     */
    public static Matcher<OptionalDouble> optionalDoubleHasValue(double value) {
        return optionalDoubleHasValue(equalTo(value));
    }
}
