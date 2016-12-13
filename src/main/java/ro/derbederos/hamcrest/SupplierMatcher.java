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

import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static ro.derbederos.hamcrest.MappedValueMatcher.getFeatureTypeName;
import static ro.derbederos.hamcrest.RetryMatchers.retry;

/**
 * @since 0.9
 */
public final class SupplierMatcher {

    /**
     * @since 0.9
     */
    public static <T> Matcher<Supplier<T>> supplierMatcher(Supplier<T> supplier, Matcher<? super T> matcher) {
        Objects.requireNonNull(supplier);
        String featureTypeName = getFeatureTypeName(supplier.getClass(), Supplier.class, 0);
        String featureDescription = "an " + featureTypeName;
        return new MappedValueMatcher<>(Supplier::get, Supplier.class, featureDescription, featureTypeName, matcher);
    }

    /**
     * @since 0.9
     */
    public static <T> Matcher<Supplier<T>> retrySupplier(Supplier<T> supplier, long durationMillis, Matcher<? super T> matcher) {
        return retry(durationMillis, supplierMatcher(supplier, matcher));
    }

    /**
     * @since 0.9
     */
    public static <T> void lambdaAssert(Supplier<T> supplier, Matcher<? super T> matcher) {
        assertThat(supplier, supplierMatcher(supplier, matcher));
    }

    /**
     * @since 0.9
     */
    public static <T> void lambdaAssert(Supplier<T> supplier, long durationMillis, Matcher<? super T> matcher) {
        assertThat(supplier, retrySupplier(supplier, durationMillis, matcher));
    }
}
