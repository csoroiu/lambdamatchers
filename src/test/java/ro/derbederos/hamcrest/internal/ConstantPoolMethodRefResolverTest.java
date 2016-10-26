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

package ro.derbederos.hamcrest.internal;

import java8.util.function.Function;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.theInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assume.assumeThat;

public class ConstantPoolMethodRefResolverTest {
    private ConstantPoolMethodRefResolver resolver = new ConstantPoolMethodRefResolver();

    @Test
    public void simple() {
        Function function = a -> a;
        Member actual = resolver.resolveMethodReference(Function.class, function.getClass());
        assertThat(actual, instanceOf(Method.class));
        Method method = (Method) actual;
        assertThat(method.getReturnType(), theInstance(Object.class));
        assertThat(method.getParameterTypes()[0], theInstance(Object.class));
    }
}