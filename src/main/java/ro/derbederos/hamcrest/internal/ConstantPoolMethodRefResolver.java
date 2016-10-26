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

import sun.misc.Unsafe;

import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;

import static ro.derbederos.hamcrest.internal.Utils.*;

class ConstantPoolMethodRefResolver implements MethodRefResolver {

    private static boolean SUPPORTED = false;

    private static Method GET_CONSTANT_POOL;
    private static Method GET_CONSTANT_POOL_SIZE;
    private static Method GET_CONSTANT_POOL_METHOD_AT;

    static {
        Unsafe unsafe = null;
        try {
            try {
                unsafe = Unsafe.getUnsafe();
            } catch (SecurityException e) {
                final PrivilegedExceptionAction<Unsafe> action =
                        () -> {
                            final Field f = Unsafe.class.getDeclaredField("theUnsafe");
                            f.setAccessible(true);

                            return (Unsafe) f.get(null);
                        };
                unsafe = AccessController.doPrivileged(action);
            }
        } catch (Exception ignored) {
        }

        if (unsafe != null) {
            try {
                GET_CONSTANT_POOL = Class.class.getDeclaredMethod("getConstantPool");
                String constantPoolName = JAVA_VERSION < 9 ? "sun.reflect.ConstantPool" : "jdk.internal.reflect.ConstantPool";
                Class<?> constantPoolClass = Class.forName(constantPoolName);
                GET_CONSTANT_POOL_SIZE = constantPoolClass.getDeclaredMethod("getSize");
                GET_CONSTANT_POOL_METHOD_AT = constantPoolClass.getDeclaredMethod("getMethodAt", int.class);

                // setting the methods as accessible
                Field overrideField = AccessibleObject.class.getDeclaredField("override");
                long overrideFieldOffset = unsafe.objectFieldOffset(overrideField);
                unsafe.putBoolean(GET_CONSTANT_POOL, overrideFieldOffset, true);
                unsafe.putBoolean(GET_CONSTANT_POOL_SIZE, overrideFieldOffset, true);
                unsafe.putBoolean(GET_CONSTANT_POOL_METHOD_AT, overrideFieldOffset, true);

                // additional checks - make sure we get a result when invoking the Class::getConstantPool and
                // ConstantPool::getSize on a class
                Object constantPool = GET_CONSTANT_POOL.invoke(Object.class);
                GET_CONSTANT_POOL_SIZE.invoke(constantPool);

                SUPPORTED = true;
            } catch (NoSuchMethodException ignore) {
            } catch (NoSuchFieldException ignore) {
            } catch (InvocationTargetException ignore) {
            } catch (IllegalAccessException ignore) {
            } catch (ClassNotFoundException ignore) {
            }
        }
    }

    @Override
    public boolean isAvailable() {
        return SUPPORTED;
    }

    @Override
    public boolean supportsClass(Class<?> lambdaClass) {
        return SUPPORTED && isLambdaClass(lambdaClass);
    }

    @Override
    public Member resolveMethodReference(Class<?> functionalInterface, Class<?> lambdaClass) {
        if (!supportsClass(lambdaClass)) {
            return null;
        }
        return getMemberRef(lambdaClass);
    }

    private static Member getMemberRef(Class<?> type) {
        // InputStream is = type.getClassLoader().getResourceAsStream(type.getName().replace('.', '/') + ".class");
        Member[] constantPoolMethods = extractConstantPoolMethods(type);
        int start = constantPoolMethods.length;
        if (isInstrumentedByJacoco(type)) {
            for (int i = constantPoolMethods.length - 1; i >= 0; i--) {
                if (constantPoolMethods[i].getName().startsWith("$jacoco")) {
                    start = i;
                }
            }
        }

        Member result = null;
        for (int i = start - 1; i >= 0; i--) {
            Member member = constantPoolMethods[i];
            // Skip SerializedLambda constructors and members of the "type" class
            if ((member instanceof Constructor
                 && member.getDeclaringClass().getName().equals("java.lang.invoke.SerializedLambda"))
                || member.getDeclaringClass().equals(type)) {
                continue;
            }

            result = member;

            // Return if not valueOf method
            if (!(member instanceof Method) || !isBoxingOrUnboxingMethod((Method) member)) {
                break;
            }
        }

        return result;
    }

    private static int getConstantPoolSize(Object constantPool) {
        try {
            return (Integer) GET_CONSTANT_POOL_SIZE.invoke(constantPool);
        } catch (InvocationTargetException ignore) {
        } catch (IllegalAccessException ignore) {
        }
        return 0;
    }

    private static Member getConstantPoolMethodAt(Object constantPool, int i) {
        try {
            return (Member) GET_CONSTANT_POOL_METHOD_AT.invoke(constantPool, i);
        } catch (InvocationTargetException ignore) {
        } catch (IllegalAccessException ignore) {
        }
        return null;
    }

    private static Member[] extractConstantPoolMethods(Class<?> type) {
        Object constantPool;
        try {
            constantPool = GET_CONSTANT_POOL.invoke(type);
        } catch (Exception ignore) {
            return new Member[0];
        }
        ArrayList<Member> methods = new ArrayList<>();
        for (int i = 0; i < getConstantPoolSize(constantPool); i++) {
            Member method = getConstantPoolMethodAt(constantPool, i);
            if (method != null) {
                methods.add(method);
            }
        }
        return methods.toArray(new Member[methods.size()]);
    }
}
