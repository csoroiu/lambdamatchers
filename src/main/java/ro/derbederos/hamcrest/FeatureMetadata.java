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

class FeatureMetadata<T> {
    final Class<T> declaringEntityType;
    final String declaringEntityName;
    final String featureName;

    FeatureMetadata(Class<T> declaringEntityType, String declaringEntityName, String featureName) {
        this.declaringEntityType = declaringEntityType;
        this.declaringEntityName = declaringEntityName;
        this.featureName = featureName;
    }

    Class<T> getDeclaringEntityType() {
        return declaringEntityType;
    }

    String getDeclaringEntityName() {
        return declaringEntityName;
    }

    String getFeatureName() {
        return featureName;
    }
}