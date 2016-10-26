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

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class UtilsTest {
    @FunctionalInterface
    interface I1<F, T> {
        T apply(F f1);

        int hashCode();

        boolean equals(Object other);

        String toString();
    }

    @FunctionalInterface
    interface I2 extends I1<String, Integer> {
        @Override
        Integer apply(String f1);
    }

    @FunctionalInterface
    interface I3 extends I1<String, Integer> {
    }

    @Test
    public void testGetSingleNameMethods() throws Exception {
        Method expected = safeGetMethod(I1.class, "apply", Object.class);
        Method actual = Utils.getSingleAbstractMethod(I1.class);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testGetSingleNameMethodsExtendsOverrides() throws Exception {
        Method expected = safeGetMethod(I2.class, "apply", String.class);

        Method actual = Utils.getSingleAbstractMethod(I2.class);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testGetSingleNameMethodsExtendsNotOverrides() throws Exception {
        Method expected = safeGetMethod(I3.class, "apply", Object.class);

        Method actual = Utils.getSingleAbstractMethod(I3.class);

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testIsObjectMethod() {
        for (Method m : extractOverridableObjectMethods()) {
            assertThat(String.valueOf(m), Utils.isObjectMethod(m), equalTo(true));
        }
    }

    private static boolean isOverridableMethod(Method m) {
        return Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers()) &&
                !Modifier.isFinal(m.getModifiers());
    }

    private static Iterable<Method> extractOverridableObjectMethods() {
        List<Method> result = new ArrayList<>();
        for (Method m : Object.class.getDeclaredMethods()) {
            if (isOverridableMethod(m)) {
                result.add(m);
            }
        }
        return result;
    }

    private static Method safeGetMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException ignore) {
            return null;
        }
    }
}