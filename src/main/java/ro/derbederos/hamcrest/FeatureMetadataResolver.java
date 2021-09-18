/*
 * Copyright (c) 2016-2021 Claudiu Soroiu
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

import static java.util.Objects.requireNonNull;

class FeatureMetadataResolver {

    private FeatureMetadataResolver() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static <T, F> FeatureMetadata<T> resolve(F featureExtractor, Class<F> functionInterface) {
        requireNonNull(featureExtractor);
        requireNonNull(functionInterface);
        if (!(functionInterface.isAssignableFrom(featureExtractor.getClass()))) {
            throw new IllegalArgumentException("featureExtractor type (" + featureExtractor.getClass() +
                    ") must be an instace of " + functionInterface);
        }
        Class<?>[] rawArguments = TypeResolver.resolveRawArguments(functionInterface, featureExtractor.getClass());

        Class<?> entityTypeClass = getIfUnknownType(rawArguments[0],
                rawArguments[0],
                Object.class);
        String entityTypeString = getIfUnknownType(rawArguments[0],
                entityTypeClass.getSimpleName(),
                "UnknownObjectType");

        String featureName = MethodRefResolver.resolveMethodRefName(featureExtractor.getClass());
        if (featureName == null) {
            Class<?> featureType = rawArguments[rawArguments.length - 1];
            featureName = getIfUnknownType(featureType,
                    featureType.getSimpleName(), "UnknownFieldType");
        }
        @SuppressWarnings("unchecked")
        Class<T> castEntityTypeClass = (Class<T>) entityTypeClass;
        return new FeatureMetadata<>(castEntityTypeClass, entityTypeString, featureName);
    }

    private static <T> T getIfUnknownType(Class<?> type, T originalValue, T unknownValue) {
        return TypeResolver.Unknown.class.isAssignableFrom(type) ? unknownValue : originalValue;
    }
}
