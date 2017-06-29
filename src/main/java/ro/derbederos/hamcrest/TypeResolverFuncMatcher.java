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
import org.hamcrest.Matcher;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

final class TypeResolverFuncMatcher {

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
        @SuppressWarnings("unchecked")
        Class<T> castInputType = (Class<T>) inputType;
        return MatcherBuilder.mappedBy(mapper, castInputType, featureDescription, featureTypeName, matcher);
    }

    static <T> Matcher<Supplier<T>> supplierMatcher(Supplier<T> supplier, Matcher<? super T> matcher) {
        supplier = requireNonNull(supplier);
        matcher = requireNonNull(matcher);
        String featureTypeName = getFeatureTypeName(supplier, Supplier.class, 0);
        String featureDescription = getArticle(featureTypeName) + " " + featureTypeName;
        return MatcherBuilder.mappedBy(Supplier::get, Supplier.class, featureDescription, featureTypeName, matcher);
    }
}