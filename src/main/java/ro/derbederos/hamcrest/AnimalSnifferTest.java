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

import java8.util.function.Supplier;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("Since15")
public class AnimalSnifferTest {
    public Supplier<Boolean> methodReference(Optional<?> o) {
        return o::isPresent;
    }

    public Predicate<Optional<?>> methodReferenceSerializable() {
        return (Serializable & Predicate<Optional<?>>) Optional::isPresent;
    }

    //    @Java8API //annotation  is not propagated to lambda
    public Predicate<Optional<?>> lambda() {
        return o -> o.isPresent();
    }

    //
//    @Java8API //annotation  is not propagated to lambda
    public Predicate<Optional<?>> lambdaSerializable() {
        return (Serializable & Predicate<Optional<?>>) o -> o.isPresent();
    }

    public void callMethodWhichReturnsIllegalType() {
        lambda();
    }

    public static void emptyMethod() {
    }

    //    @Java8API
    public Predicate<?>[][][] arrayReturnType() {
        return new Predicate[][][]{{{(Predicate<Optional<?>>) Optional::isPresent}}};
    }

    public void callArray() {
        arrayReturnType();
    }

    public void exception() {
        try {
            Method method = AnimalSnifferTest.class.getDeclaredMethod("emptyMethod");
            method.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {
        } finally {
            System.out.println("all good");
        }

    }

//    public void exception2() {
//        try {
//            Method method = AnimalSnifferTest.class.getDeclaredMethod("emptyMethod");
//            method.invoke(null);
//        } catch (ReflectiveOperationException ignore) {
//        }
//    }

}
