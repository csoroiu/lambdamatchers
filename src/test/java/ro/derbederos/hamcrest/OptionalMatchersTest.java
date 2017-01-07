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

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assume.assumeThat;
import static ro.derbederos.hamcrest.LambdaMatchers.mappedBy;
import static ro.derbederos.hamcrest.OptionalMatchers.*;

public class OptionalMatchersTest {

    @Before
    public void before() throws Exception {
        Double JAVA_VERSION = Double.parseDouble(System.getProperty("java.specification.version", "0"));
        assumeThat("Java version", JAVA_VERSION, greaterThanOrEqualTo(1.8d));
    }

    @Test
    public void testOptionalPresent() throws Exception {
        assertThat(Optional.of("abc"), optionalIsPresent());
    }

    @Test
    public void testOptionalEmpty() throws Exception {
        assertThat(Optional.empty(), optionalIsEmpty());
    }

    @Test
    public void testOptionalHasValue() throws Exception {
        assertThat(Optional.of("abc"), optionalHasValue("abc"));
    }

    @Test
    public void testOptionalValueWithMap() throws Exception {
        assertThat(Optional.of("abc"), mappedBy(Optional::get, equalTo("abc")));
    }

    @Test
    public void testOptionalIntPresent() throws Exception {
        assertThat(OptionalInt.of(4), optionalIntIsPresent());
    }

    @Test
    public void testOptionalIntEmpty() throws Exception {
        assertThat(OptionalInt.empty(), optionalIntIsEmpty());
    }

    @Test
    public void testOptionalIntHasValue() throws Exception {
        assertThat(OptionalInt.of(4), optionalIntHasValue(4));
    }

    @Test
    public void testOptionalLongPresent() throws Exception {
        assertThat(OptionalLong.of(4L), optionalLongIsPresent());
    }

    @Test
    public void testOptionalLongEmpty() throws Exception {
        assertThat(OptionalLong.empty(), optionalLongIsEmpty());
    }

    @Test
    public void testOptionalLongHasValue() throws Exception {
        assertThat(OptionalLong.of(4L), optionalLongHasValue(4L));
    }

    @Test
    public void testOptionalDoublePresent() throws Exception {
        assertThat(OptionalDouble.of(4.0), optionalDoubleIsPresent());
    }

    @Test
    public void testOptionalDoubleEmpty() throws Exception {
        assertThat(OptionalDouble.empty(), optionalDoubleIsEmpty());
    }

    @Test
    public void testOptionalDoubleHasValue() throws Exception {
        assertThat(OptionalDouble.of(4.0), optionalDoubleHasValue(4.0));
    }
}