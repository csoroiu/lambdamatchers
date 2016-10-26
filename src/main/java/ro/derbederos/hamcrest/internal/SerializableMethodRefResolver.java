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

import ro.derbederos.hamcrest.Java8API;

import java.io.Serializable;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import static ro.derbederos.hamcrest.internal.Utils.JAVA_VERSION;
import static ro.derbederos.hamcrest.internal.Utils.isLambdaClass;

@SuppressWarnings("Since15")
@Java8API
class SerializableMethodRefResolver implements MethodRefResolver {

    @Override
    public boolean isAvailable() {
        return JAVA_VERSION >= 1.8;
    }

    @Override
    public boolean supportsClass(Class<?> lambdaClass) {
        return isAvailable() && Serializable.class.isAssignableFrom(lambdaClass) && isLambdaClass(lambdaClass);
    }

    @Override
    public Member resolveMethodReference(Class<?> functionalInterface, Class<?> lambdaClass) {
        if (!supportsClass(lambdaClass)) {
            return null;
        }
        return resolveMethodReference((Serializable) createDummyInstance(lambdaClass));
    }

    private Member resolveMethodReference(Serializable lambdaFunction) {
        if (lambdaFunction == null) {
            return null;
        }
        try {
            return getReflectedMethod(lambdaFunction);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | ClassCastException ignore) {
        }
        return null;
    }

    private static Object createDummyInstance(Class<?> lambdaClass) {
        try {
            Constructor<?>[] ctrs = lambdaClass.getDeclaredConstructors();
            if (ctrs.length == 1) {
                Constructor<?> constructor = ctrs[0];
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                Object[] values = new Object[parameterTypes.length];
                for (int i = 0; i < values.length; i++) {
                    values[i] = getDefaultValue(parameterTypes[i]);
                }
                try {
                    constructor.setAccessible(true);
                    return constructor.newInstance(values);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignore) {
                }
            }
        } catch (SecurityException ignore) {
        }
        return null;
    }

    private static Object getDefaultValue(Class<?> clazz) {
        if (clazz.equals(Boolean.TYPE)) {
            return Boolean.FALSE;
        } else if (clazz.equals(Byte.TYPE)) {
            return Byte.valueOf((byte) 0);
        } else if (clazz.equals(Character.TYPE)) {
            return Character.valueOf((char) 0);
        } else if (clazz.equals(Short.TYPE)) {
            return Short.valueOf((short) 0);
        } else if (clazz.equals(Integer.TYPE)) {
            return Integer.valueOf(0);
        } else if (clazz.equals(Long.TYPE)) {
            return Long.valueOf(0L);
        } else if (clazz.equals(Float.TYPE)) {
            return Float.valueOf(0f);
        } else if (clazz.equals(Double.TYPE)) {
            return Double.valueOf(0d);
        } else {
            return null;
        }
    }

    private static Member getReflectedMethod(Serializable lambda) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        Object serialized = getSerializedLambda(lambda);
        if (!(serialized instanceof SerializedLambda)) {
            return null;
        }
        SerializedLambda serializedLambda = (SerializedLambda) serialized;
        ClassLoader classLoader = lambda.getClass().getClassLoader();
        Class<?> containingClass = Class.forName(serializedLambda.getImplClass().replace('/', '.'), false, classLoader);
        MethodType methodType = MethodType.fromMethodDescriptorString(serializedLambda.getImplMethodSignature(), classLoader);
        Class<?>[] parameters = methodType.parameterArray();
        if (serializedLambda.getImplMethodKind() == 8) { //MethodHandleNatives.Constants.REF_newInvokeSpecial - constructor
            return containingClass.getDeclaredConstructor(parameters);
        } else {
            return containingClass.getDeclaredMethod(serializedLambda.getImplMethodName(), parameters);
        }
    }

    private static Object getSerializedLambda(Serializable lambda) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method method = lambda.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(true);
        return method.invoke(lambda);
    }
}
