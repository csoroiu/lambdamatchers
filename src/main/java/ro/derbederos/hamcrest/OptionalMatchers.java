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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static ro.derbederos.hamcrest.FunctionMatcher.map;

public class OptionalMatchers {

    private OptionalMatchers() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Matcher<Optional<?>> optionalIsPresent() {
        return map(Optional::isPresent, "Optional.isPresent", "", equalTo(true));
    }

    public static Matcher<Optional<?>> optionalIsEmpty() {
        return map(Optional::isPresent, "Optional.isPresent", "", equalTo(false));
    }

    public static <T> Matcher<Optional<T>> optionalHasValue(Matcher<? super T> matcher) {
        return allOf(optionalIsPresent(), map(Optional::get, "Optional item:", "", matcher));
    }

    public static <T> Matcher<Optional<T>> optionalHasValue(T value) {
        return optionalHasValue(equalTo(value));
    }

    public static Matcher<OptionalInt> optionalIntIsPresent() {
        return map(OptionalInt::isPresent, "OptionalInt.isPresent", "", equalTo(true));
    }

    public static Matcher<OptionalInt> optionalIntIsEmpty() {
        return map(OptionalInt::isPresent, "OptionalInt.isPresent", "", equalTo(false));
    }

    public static <T> Matcher<OptionalInt> optionalIntHasValue(Matcher<Integer> matcher) {
        return allOf(optionalIntIsPresent(), map(OptionalInt::getAsInt, "OptionalInt item:", "", matcher));
    }

    public static <T> Matcher<OptionalInt> optionalIntHasValue(int value) {
        return optionalIntHasValue(equalTo(value));
    }

    public static Matcher<OptionalLong> optionalLongIsPresent() {
        return map(OptionalLong::isPresent, "OptionalLong.isPresent", "", equalTo(true));
    }

    public static Matcher<OptionalLong> optionalLongIsEmpty() {
        return map(OptionalLong::isPresent, "OptionalLong.isPresent", "", equalTo(false));
    }

    public static <T> Matcher<OptionalLong> optionalLongHasValue(Matcher<Long> matcher) {
        return allOf(optionalLongIsPresent(), map(OptionalLong::getAsLong, "OptionalLong item:", "", matcher));
    }

    public static <T> Matcher<OptionalLong> optionalLongHasValue(long value) {
        return optionalLongHasValue(equalTo(value));
    }

    public static Matcher<OptionalDouble> optionalDoubleIsPresent() {
        return map(OptionalDouble::isPresent, "OptionalDouble.isPresent", "", equalTo(true));
    }

    public static Matcher<OptionalDouble> optionalDoubleIsEmpty() {
        return map(OptionalDouble::isPresent, "OptionalDouble.isPresent", "", equalTo(false));
    }

    public static <T> Matcher<OptionalDouble> optionalDoubleHasValue(Matcher<Double> matcher) {
        return allOf(optionalDoubleIsPresent(), map(OptionalDouble::getAsDouble, "OptionalDouble item: ", "", matcher));
    }

    public static <T> Matcher<OptionalDouble> optionalDoubleHasValue(double value) {
        return optionalDoubleHasValue(equalTo(value));
    }
}
