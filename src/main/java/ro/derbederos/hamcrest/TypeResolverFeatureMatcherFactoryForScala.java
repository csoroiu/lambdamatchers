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
import scala.Function1;

class TypeResolverFeatureMatcherFactoryForScala extends TypeResolverFeatureMatcherFactory {

    static <T, U> Matcher<T> feature(Function1<? super T, ? extends U> featureExtractor,
                                     Matcher<? super U> featureMatcher) {
        FeatureMetadata<T> featureMetadata = FeatureMetadataResolver
                .resolve(featureExtractor, Function1.class);
        return FeatureMatcherFactory.feature(featureMetadata.getDeclaringEntityType(),
                getFeatureDescription(featureMetadata.getFeatureName(), featureMetadata.getDeclaringEntityName()),
                featureMetadata.getFeatureName(),
                featureExtractor::apply,
                featureMatcher);
    }

    static <T, U> Matcher<T> feature(String featureName,
                                     Function1<? super T, ? extends U> featureExtractor,
                                     Matcher<? super U> featureMatcher) {
        FeatureMetadata<T> featureMetadata = FeatureMetadataResolver
                .resolve(featureExtractor, Function1.class);
        return FeatureMatcherFactory.feature(featureMetadata.getDeclaringEntityType(),
                getFeatureDescription(featureName, featureMetadata.getDeclaringEntityName()),
                featureName,
                featureExtractor::apply,
                featureMatcher);
    }
}
