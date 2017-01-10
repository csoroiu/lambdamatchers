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

import _shaded.net.jodah.typetools.TypeResolver;
import java8.util.function.Function;
import java8.util.function.Supplier;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static java8.util.Objects.requireNonNull;

final class MappedValueMatcher<T, U> extends TypeSafeMatcher<T> {
    private final String featureDescription;
    private final String featureName;
    private final Function<? super T, ? extends U> mapper;
    private final Matcher<? super U> subMatcher;
    private T lastInput = null;
    private U lastValue = null;

    private MappedValueMatcher(Function<? super T, ? extends U> mapper, Class<?> inputType,
                               String featureDescription, String featureName, Matcher<? super U> subMatcher) {
        super(requireNonNull(inputType));
        this.mapper = requireNonNull(mapper);
        this.subMatcher = requireNonNull(subMatcher);
        this.featureDescription = requireNonNull(featureDescription);
        this.featureName = requireNonNull(featureName);
    }

    @Override
    public void describeTo(Description description) {
        if (featureDescription.length() > 0) {
            description.appendText(featureDescription).appendText(" ");
        }
        description.appendDescriptionOf(subMatcher);
    }

    @Override
    protected void describeMismatchSafely(T actual, Description mismatch) {
        if (featureName.length() > 0) {
            mismatch.appendText(featureName).appendText(" ");
        }
        U value = actual == lastInput ? lastValue : mapper.apply(actual);
        subMatcher.describeMismatch(value, mismatch);
    }

    @Override
    protected final boolean matchesSafely(T actual) {
        // hack - cache the description, or else it won't work with streams correctly.
        lastValue = mapper.apply(actual);
        lastInput = actual;
        return subMatcher.matches(lastValue);
    }

    private static <T> String getFeatureTypeName(T mapper, Class<T> mapperInterface, int resultIndex) {
        mapper = requireNonNull(mapper);
        String featureTypeName = MethodRefResolver.resolveMethodRefName(mapper.getClass());
        if (featureTypeName == null) {
            Class<?> featureType = TypeResolver.resolveRawArguments(mapperInterface, mapper.getClass())[resultIndex];
            featureTypeName = featureType.getSimpleName();
            if (TypeResolver.Unknown.class.isAssignableFrom(featureType)) {
                featureTypeName = "UnknownFieldType";
            }
        }
        return featureTypeName;
    }

    private static String getArticle(String objectTypeName) {
        boolean startsWithVowel = "AaEeIiOoUu".indexOf(objectTypeName.charAt(0)) >= 0;
        return startsWithVowel ? "an" : "a";
    }

    static <T, U> Matcher<T> mappedBy(Function<? super T, ? extends U> mapper, Matcher<? super U> matcher) {
        return mappedBy(mapper, getFeatureTypeName(mapper, Function.class, 1), matcher);
    }

    static <T, U> Matcher<T> mappedBy(Function<? super T, ? extends U> mapper, String featureTypeName, Matcher<? super U> matcher) {
        mapper = requireNonNull(mapper);
        matcher = requireNonNull(matcher);
        featureTypeName = requireNonNull(featureTypeName);
        Class<?> inputType = TypeResolver.resolveRawArguments(Function.class, mapper.getClass())[0];
        String objectTypeName = inputType.getSimpleName();
        if (TypeResolver.Unknown.class.isAssignableFrom(inputType)) {
            inputType = Object.class;
            objectTypeName = "UnknownObjectType";
        }
        String featureDescription = getArticle(objectTypeName) + " " + objectTypeName + " having " + featureTypeName;
        return new MappedValueMatcher<>(mapper, inputType, featureDescription, featureTypeName, matcher);
    }

    static <T> Matcher<Supplier<T>> supplierMatcher(Supplier<T> supplier, Matcher<? super T> matcher) {
        supplier = requireNonNull(supplier);
        matcher = requireNonNull(matcher);
        String featureTypeName = getFeatureTypeName(supplier, Supplier.class, 0);
        String featureDescription = "a " + featureTypeName;
        return new MappedValueMatcher<>(Supplier::get, Supplier.class, featureDescription, featureTypeName, matcher);
    }
}