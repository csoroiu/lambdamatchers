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

import net.jodah.typetools.TypeResolver;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;
import java.util.function.Function;

final class FunctionMatcher<T, U> extends TypeSafeMatcher<T> {
    private final String featureDescription;
    private final String featureName;
    private final Function<T, U> mapper;
    private final Matcher<? super U> subMatcher;
    private T lastInput = null;
    private U lastValue = null;

    private FunctionMatcher(Function<T, U> mapper, String featureDescription, String featureName, Matcher<? super U> subMatcher) {
        super(resolveInputType(Objects.requireNonNull(mapper)));
        this.mapper = mapper;
        this.subMatcher = Objects.requireNonNull(subMatcher);
        this.featureDescription = Objects.toString(featureDescription, "").trim();
        this.featureName = Objects.toString(featureName, "").trim();
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

    private static <T, U> Class<?> resolveInputType(Function<T, U> mapper) {
        Class<?>[] arguments = TypeResolver.resolveRawArguments(Function.class, mapper.getClass());
        Class<?> type = arguments[0];
        if (TypeResolver.Unknown.class.isAssignableFrom(type)) {
            type = Object.class;
        }
        return type;
    }

    static <T, U> Matcher<T> map(Function<T, U> mapper, String featureDescription, String featureName, Matcher<? super U> subMatcher) {
        return new FunctionMatcher<>(mapper, featureDescription, featureName, subMatcher);
    }

    static <T, U> Matcher<T> map(Function<T, U> mapper, Matcher<? super U> matcher) {
        Objects.requireNonNull(mapper);
        Class<?>[] arguments = TypeResolver.resolveRawArguments(Function.class, mapper.getClass());
        String objectTypeName = arguments[0].getSimpleName();
        String featureTypeName = arguments[1].getSimpleName();
        if (TypeResolver.Unknown.class.isAssignableFrom(arguments[0])) {
            objectTypeName = "UnknownObjectType";
        }
        if (TypeResolver.Unknown.class.isAssignableFrom(arguments[1])) {
            featureTypeName = "UnknownFieldType";
        }
        boolean startsWithVowel = "AaEeIiOoUu".indexOf(objectTypeName.charAt(0)) >= 0;
        String article = startsWithVowel ? "an" : "a";
        return map(mapper,
                article + " " + objectTypeName + " having " + featureTypeName,
                featureTypeName,
                matcher);
    }
}
