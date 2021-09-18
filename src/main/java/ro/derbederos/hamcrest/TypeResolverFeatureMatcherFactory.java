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

import org.hamcrest.Matcher;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;

class TypeResolverFeatureMatcherFactory {

    private TypeResolverFeatureMatcherFactory() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static String getArticle(String s) {
        boolean startsWithVowel = "AaEeIiOoUu".indexOf(s.charAt(0)) >= 0;
        return startsWithVowel ? "an" : "a";
    }

    static String getFeatureDescription(String featureName, String entityType) {
        return getArticle(entityType) + " " + entityType + " having " + featureName;
    }

    static <T, U> Matcher<T> feature(Function<? super T, ? extends U> featureExtractor,
                                     Matcher<? super U> featureMatcher) {
        FeatureMetadata<T> featureMetadata = FeatureMetadataResolver
                .resolve(featureExtractor, Function.class);
        return FeatureMatcherFactory.feature(featureMetadata.getDeclaringEntityType(),
                getFeatureDescription(featureMetadata.getFeatureName(), featureMetadata.getDeclaringEntityName()),
                featureMetadata.getFeatureName(),
                featureExtractor,
                featureMatcher);
    }

    static <T, U> Matcher<T> feature(String featureName,
                                     Function<? super T, ? extends U> featureExtractor,
                                     Matcher<? super U> featureMatcher) {
        FeatureMetadata<T> featureMetadata = FeatureMetadataResolver
                .resolve(featureExtractor, Function.class);
        return FeatureMatcherFactory.feature(featureMetadata.getDeclaringEntityType(),
                getFeatureDescription(featureName, featureMetadata.getDeclaringEntityName()),
                featureName,
                featureExtractor,
                featureMatcher);
    }

    static <T, U> Matcher<Iterable<T>> featureIterable(Function<? super T, ? extends U> featureExtractor,
                                                       Matcher<? extends Iterable<? super U>> iterableMatcher) {

        FeatureMetadata<T> featureMetadata = FeatureMetadataResolver
                .resolve(featureExtractor, Function.class);
        String entityType = Iterable.class.getSimpleName() + " of " + featureMetadata.getDeclaringEntityName();
        return FeatureMatcherFactory.feature(Iterable.class,
                getFeatureDescription(featureMetadata.getFeatureName(),
                        entityType),
                featureMetadata.getFeatureName(),
                iterable -> iterableWrapper(featureExtractor, iterable),
                iterableMatcher);
    }

    private static <T, U> Iterable<U> iterableWrapper(Function<? super T, U> featureExtractor,
                                                      Iterable<? extends T> iterable) {
        return () -> StreamSupport.stream(iterable.spliterator(), false)
                .map(featureExtractor).iterator();
    }

    static <T, U> Matcher<T[]> featureArray(Function<? super T, ? extends U> featureExtractor,
                                            Matcher<? extends Iterable<? super U>> iterableMatcher) {

        FeatureMetadata<T> featureMetadata = FeatureMetadataResolver
                .resolve(featureExtractor, Function.class);
        String entityType = Object[].class.getSimpleName() + " of " + featureMetadata.getDeclaringEntityName();
        return FeatureMatcherFactory.feature(Object[].class,
                getFeatureDescription(featureMetadata.getFeatureName(),
                        entityType),
                featureMetadata.getFeatureName(),
                array -> arrayWrapper(featureExtractor, array),
                iterableMatcher);
    }

    private static <T, U> Iterable<U> arrayWrapper(Function<? super T, U> featureExtractor,
                                                   T[] array) {
        return () -> Arrays.stream(array).map(featureExtractor).iterator();
    }

    static <T> Matcher<Supplier<T>> supplierMatcher(Supplier<T> supplier, Matcher<? super T> matcher) {
        requireNonNull(supplier);
        requireNonNull(matcher);

        String featureName = FeatureMetadataResolver.resolve(supplier, Supplier.class).getFeatureName();
        String featureDescription = getArticle(featureName) + " " + featureName;
        return FeatureMatcherFactory.feature(Supplier.class, featureDescription, featureName, Supplier::get, matcher);
    }
}