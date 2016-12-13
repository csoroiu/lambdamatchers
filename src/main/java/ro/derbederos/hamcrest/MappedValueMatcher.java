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

import _shaded.net.jodah.typetools.TypeResolver;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;

final class MappedValueMatcher<T, U> extends TypeSafeMatcher<T> {
    private final String featureDescription;
    private final String featureName;
    private final Function<T, U> mapper;
    private final Matcher<? super U> subMatcher;
    private T lastInput = null;
    private U lastValue = null;

    MappedValueMatcher(Function<T, U> mapper, Class<?> inputType, String featureDescription, String featureName, Matcher<? super U> subMatcher) {
        super(inputType);
        this.mapper = Objects.requireNonNull(mapper);
        this.subMatcher = Objects.requireNonNull(subMatcher);
        this.featureDescription = Objects.requireNonNull(featureDescription);
        this.featureName = Objects.requireNonNull(featureName);
    }

    @Override
    public void describeTo(Description description) {
        if (!featureDescription.isEmpty()) {
            description.appendText(featureDescription).appendText(" ");
        }
        description.appendDescriptionOf(subMatcher);
    }

    @Override
    protected void describeMismatchSafely(T actual, Description mismatch) {
        if (!featureName.isEmpty()) {
            mismatch.appendText(featureName).appendText(" ");
        }
        U value = actual == lastInput ? lastValue : mapper.apply(actual);
        subMatcher.describeMismatch(value, mismatch);
    }

    @Override
    protected final boolean matchesSafely(T actual) {
        //hack - cache the description, or else it won't work with streams correctly.
        lastValue = mapper.apply(actual);
        lastInput = actual;
        return subMatcher.matches(lastValue);
    }

    static <T, U> Matcher<T> mappedBy(Function<T, U> mapper, Matcher<? super U> matcher) {
        return mappedBy(Objects.requireNonNull(mapper), getFeatureTypeName(mapper.getClass(), Function.class, 1), matcher);
    }

    static <T> String getFeatureTypeName(Class<? extends T> mapperClass, Class<T> mapperInterface, int resultIndex) {
        String featureTypeName = MethodRefResolver.resolveMethodRefName(mapperClass);
        if (featureTypeName == null) {
            Class<?> featureType = TypeResolver.resolveRawArguments(mapperInterface, mapperClass)[resultIndex];
            featureTypeName = featureType.getSimpleName();
            if (TypeResolver.Unknown.class.isAssignableFrom(featureType)) {
                featureTypeName = "UnknownFieldType";
            }
        }
        return featureTypeName;
    }

    static <T, U> Matcher<T> mappedBy(Function<T, U> mapper, String featureTypeName, Matcher<? super U> matcher) {
        Objects.requireNonNull(mapper);
        Class<?> inputType = TypeResolver.resolveRawArguments(Function.class, mapper.getClass())[0];
        String objectTypeName = inputType.getSimpleName();
        if (TypeResolver.Unknown.class.isAssignableFrom(inputType)) {
            inputType = Object.class;
            objectTypeName = "UnknownObjectType";
        }
        String featureDescription = buildFeatureDescription(objectTypeName, featureTypeName);
        return new MappedValueMatcher<>(mapper, inputType, featureDescription, featureTypeName, matcher);
    }

    private static String buildFeatureDescription(String objectTypeName, String featureTypeName) {
        boolean startsWithVowel = "AaEeIiOoUu".indexOf(objectTypeName.charAt(0)) >= 0;
        String article = startsWithVowel ? "an" : "a";
        return article + " " + objectTypeName + " having " + featureTypeName;
    }
}