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
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertDescription;
import static ro.derbederos.hamcrest.MatcherDescriptionAssert.assertMismatchDescription;
import static ro.derbederos.hamcrest.SupplierMatcher.lambdaAssert;
import static ro.derbederos.hamcrest.SupplierMatcher.supplierMatcher;

public class SupplierMatcherTest {

    @Test
    public void simpleTestObjectMethodReference() {
        LambdaMatchersTest.Person p = new LambdaMatchersTest.Person("Brutus", 21);
        lambdaAssert(p::getName, equalTo("Brutus"));
    }

    @Test
    public void simpleTestObjectMethodReferenceDescription() {
        LambdaMatchersTest.Person p = new LambdaMatchersTest.Person("Brutus", 21);
        Supplier<String> supplier = p::getName;
        Matcher<Supplier<String>> matcher = supplierMatcher(supplier, equalTo("Caesar"));

        assertDescription(equalTo("an `String Person.getName()` \"Caesar\""), matcher);
        assertMismatchDescription(equalTo("`String Person.getName()` was \"Brutus\""),
                supplier, matcher);
    }

    @Test
    public void testPerson_assert_supplier() {
        LambdaMatchersTest.Person p = new LambdaMatchersTest.Person("Brutus", 21);
        lambdaAssert(p::getAge, lessThan(35));
    }
}
