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

import org.hamcrest.Matcher;
import org.hamcrest.core.SubstringMatcher;

import java.util.regex.Pattern;

final class PatternFindMatcher extends SubstringMatcher {

    private final Pattern pattern;

    private PatternFindMatcher(Pattern pattern) {
        super(pattern.pattern());
        this.pattern = pattern;
    }

    @Override
    protected boolean evalSubstringOf(String actual) {
        return pattern.matcher(actual).find();
    }

    @Override
    protected String relationship() {
        return "containing pattern";
    }

    static Matcher<String> containsPattern(Pattern pattern) {
        return new PatternFindMatcher(pattern);
    }
}
