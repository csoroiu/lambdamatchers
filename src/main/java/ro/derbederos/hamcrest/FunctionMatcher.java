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
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import java.util.Objects;
import java.util.function.Function;

final class FunctionMatcher<T, U> extends FeatureMatcher<T, U> {

    private final Function<T, U> mapper;
    private Description lastDescription;
    private boolean lastResult = true;

    private FunctionMatcher(Function<T, U> mapper, String featureDescription, String featureName, Matcher<? super U> subMatcher) {
        super(subMatcher, featureDescription, featureName);
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(subMatcher);
        this.mapper = mapper;
    }

    @Override
    protected final U featureValueOf(T actual) {
        return mapper.apply(actual);
    }

    @Override
    protected final boolean matchesSafely(T actual, Description mismatch) {
        //hack - cache the description, or else it won't work with streams correctly.
        if (!lastResult && lastDescription != null && !(mismatch instanceof Description.NullDescription)) {
            mismatch.appendText(String.valueOf(lastDescription));
            lastDescription = null;
            lastResult = true;
            return false;
        }

        lastDescription = new StringDescription();
        lastResult = super.matchesSafely(actual, lastDescription);
        mismatch.appendText(String.valueOf(lastDescription));
        return lastResult;
    }

    static <T, U> Matcher<T> map(Function<T, U> mapper, String featureDescription, String featureName, Matcher<? super U> subMatcher) {
        featureDescription = Objects.toString(featureDescription, "");
        featureName = Objects.toString(featureName, "");
        return new FunctionMatcher<>(mapper, featureDescription, featureName, subMatcher);
    }

    static <T, U> Matcher<T> map(Function<T, U> mapper, Matcher<? super U> matcher) {
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
                article + " " + objectTypeName + "::" + featureTypeName,
                featureTypeName,
                matcher);
    }
}
