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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

class Utils {

    private Utils() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static final Double JAVA_VERSION = Double.parseDouble(System.getProperty("java.specification.version", "0"));

    /**
     * Java 8 produces at runtime classes named {@code EnclosingClass$$Lambda$1}
     */
    private static class LazyLambdaClassPattern {
        private static final Pattern PATTERN =
                Pattern.compile("[^\\s\"]+|\"[^\"]*\"");
    }

    static boolean isLambdaClass(String className) {
        return className != null && LazyLambdaClassPattern.PATTERN.matcher(className).matches();
    }

    static boolean isLambdaClass(Class<?> type) {
        // https://github.com/orfjackal/retrolambda/blob/master/retrolambda/src/main/java/net/orfjackal/retrolambda/lambdas/LambdaReifier.java
        // isLambdaClassToReify method
        return type != null && type.isSynthetic() && isLambdaClass(getRealClassName(type));
    }

    static String getRealClassName(Class<?> type) {
        // {@link net.bytebuddy.description.type.TypeDescription.ForLoadedType#getName(Class)}
        // the name of a lambda class looks like: ro.derbederos.hamcrest.BiFunctionTest$$Lambda$2/1429880200
        // we need to discard everything that is after the /
        String name = type.getName();
        int anonymousLoaderIndex = name.indexOf('/');
        return anonymousLoaderIndex == -1
                ? name
                : name.substring(0, anonymousLoaderIndex);
    }

    static boolean isRetroLambdaClass(Class<?> type) {
        if (isLambdaClass(type)) {
            for (Method method : type.getDeclaredMethods())
                if (method.getName().equals("lambdaFactory$")) {
                    return true;
                }
        }
        return false;
    }

    static boolean isInstrumentedByJacoco(Class<?> type) {
        // http://www.eclemma.org/jacoco/trunk/doc/faq.html
        // JaCoCo [...] adds two members to the classes: A private static field $jacocoData and
        // a private static method $jacocoInit(). Both members are marked as synthetic.
        try {
            type.getDeclaredMethod("$jacocoInit");
            return true;
        } catch (NoSuchMethodException ignore) {
        }
        try {
            type.getDeclaredField("$jacocoData");
            return true;
        } catch (NoSuchFieldException ignore) {
        }
        return false;
    }

    static boolean isBoxingOrUnboxingMethod(Method method) {
        return isBoxingMethod(method) || isUnboxingMethod(method);
    }

    private static boolean isBoxingMethod(Method method) {
        Class<?>[] parameters = method.getParameterTypes();
        return "valueOf".equals(method.getName()) && parameters.length == 1 && parameters[0].isPrimitive()
                && wrapPrimitives(parameters[0]).equals(method.getDeclaringClass());
    }

    private static boolean isUnboxingMethod(Method method) {
        String methodName = method.getName();
        String returnType = method.getReturnType().getSimpleName();
        Class<?>[] parameters = method.getParameterTypes();

        return method.getReturnType().isPrimitive() && parameters.length == 0
                // booleanValue, byteValue, charValue, doubleValue, floatValue, intValue, longValue, shortValue
                && methodName.equals(returnType + "Value")
                && (wrapPrimitives(method.getReturnType()).equals(method.getDeclaringClass())
                || method.getDeclaringClass().equals(Number.class));
    }

    private static Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS;

    static {
        Map<Class<?>, Class<?>> types = new HashMap<>();
        types.put(boolean.class, Boolean.class);
        types.put(byte.class, Byte.class);
        types.put(char.class, Character.class);
        types.put(double.class, Double.class);
        types.put(float.class, Float.class);
        types.put(int.class, Integer.class);
        types.put(long.class, Long.class);
        types.put(short.class, Short.class);
        types.put(void.class, Void.class);
        PRIMITIVE_WRAPPERS = Collections.unmodifiableMap(types);
    }

    private static Class<?> wrapPrimitives(Class<?> clazz) {
        return clazz.isPrimitive() ? PRIMITIVE_WRAPPERS.get(clazz) : clazz;
    }

    static boolean isObjectMethod(Method m) {
        switch (m.getName()) {
            case "toString":
                return (m.getReturnType() == String.class
                        && m.getParameterTypes().length == 0);
            case "hashCode":
                return (m.getReturnType() == int.class
                        && m.getParameterTypes().length == 0);
            case "equals":
                return (m.getReturnType() == boolean.class
                        && m.getParameterTypes().length == 1
                        && m.getParameterTypes()[0] == Object.class);
        }
        return false;
    }

    // under java 7 the array contains 2 elements
    // apply(String) and apply(Object)
    // under java 8, the last one is filtered out by the {@code getSingleNameMethods}
    // because it is default non-abstract bridged synthetic method
    // assertThat("length", actual.length, equalTo(1));
    private static Method[] getSingleNameMethods(Class<?> intfc) {
        // public, non-final methods are allowed inside an interface
        ArrayList<Method> methods = new ArrayList<>();
        String uniqueName = null;
        for (Method m : intfc.getMethods()) {
            if (isObjectMethod(m)) continue;
            if (!Modifier.isAbstract(m.getModifiers())) continue;
            String mname = m.getName();
            if (uniqueName == null)
                uniqueName = mname;
            else if (!uniqueName.equals(mname))
                return null;  // too many abstract methods
            methods.add(m);
        }
        if (uniqueName == null) return null;
        return methods.toArray(new Method[methods.size()]);
    }

    static Method getSingleAbstractMethod(Class<?> intfc) {
        Method[] methods = getSingleNameMethods(intfc);
        return methods == null ? null : methods[0];
    }
}
