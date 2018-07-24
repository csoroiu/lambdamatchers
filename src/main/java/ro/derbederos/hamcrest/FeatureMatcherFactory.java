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

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.function.Function;

class FeatureMatcherFactory {
    private static void appendTextIfNotEmpty(Description description, String text) {
        if (text.length() > 0) {
            description.appendText(text).appendText(" ");
        }
    }

    static <T, U> Matcher<T> hasFeature(Class<? super T> inputType,
                                        String featureDescription,
                                        String featureName,
                                        Function<? super T, ? extends U> featureFunction,
                                        Matcher<? super U> featureMatcher) {
        return MatcherBuilder.<T>of(inputType)
                .matches(item -> featureMatcher.matches(featureFunction.apply(item)))
                .description(description -> {
                    appendTextIfNotEmpty(description, featureDescription);
                    featureMatcher.describeTo(description);
                })
                .describeMismatch((item, mismatch) -> {
                    appendTextIfNotEmpty(mismatch, featureName);
                    featureMatcher.describeMismatch(featureFunction.apply(item), mismatch);
                })
                .build();
    }
}
