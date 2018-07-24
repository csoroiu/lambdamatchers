/*
 * Copyright (c) 2016-2018 Claudiu Soroiu
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
import org.hamcrest.Matcher;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

final class TypeResolverFeatureMatcherFactory {

    private static <T> String getFeatureTypeName(T featureFunction, Class<T> functionInterface, int resultIndex) {
        featureFunction = requireNonNull(featureFunction);
        String featureTypeName = MethodRefResolver.resolveMethodRefName(featureFunction.getClass());
        if (featureTypeName == null) {
            Class<?> featureType = TypeResolver.resolveRawArguments(functionInterface, featureFunction.getClass())[resultIndex];
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

    static <T, U> Matcher<T> hasFeature(Function<? super T, ? extends U> featureFunction,
                                        Matcher<? super U> featureMatcher) {
        return hasFeature(getFeatureTypeName(featureFunction, Function.class, 1), featureFunction, featureMatcher);
    }

    static <T, U> Matcher<T> hasFeature(String featureTypeName,
                                        Function<? super T, ? extends U> featureFunction,
                                        Matcher<? super U> featureMatcher) {
        featureFunction = requireNonNull(featureFunction);
        featureMatcher = requireNonNull(featureMatcher);
        featureTypeName = requireNonNull(featureTypeName);
        Class<?> inputType = TypeResolver.resolveRawArguments(Function.class, featureFunction.getClass())[0];
        String objectTypeName = inputType.getSimpleName();
        if (TypeResolver.Unknown.class.isAssignableFrom(inputType)) {
            inputType = Object.class;
            objectTypeName = "UnknownObjectType";
        }
        String featureDescription = getArticle(objectTypeName) + " " + objectTypeName + " having " + featureTypeName;
        @SuppressWarnings("unchecked")
        Class<T> castInputType = (Class<T>) inputType;
        return FeatureMatcherFactory.hasFeature(castInputType, featureDescription, featureTypeName, featureFunction, featureMatcher);
    }

    static <T> Matcher<Supplier<T>> supplierMatcher(Supplier<T> supplier, Matcher<? super T> matcher) {
        supplier = requireNonNull(supplier);
        matcher = requireNonNull(matcher);
        String featureTypeName = getFeatureTypeName(supplier, Supplier.class, 0);
        String featureDescription = getArticle(featureTypeName) + " " + featureTypeName;
        return FeatureMatcherFactory.hasFeature(Supplier.class, featureDescription, featureTypeName, Supplier::get, matcher);
    }
}