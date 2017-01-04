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

import _shaded.net.jodah.typetools.TypeResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

class MethodRefResolver {

    private static final String DISABLE_LAMBDA_METHOD_REF = "lambdamatchers.disableMethodRefNameSupport";
    private static final String LAMBDA_METHOD_PREFIX = "lambda$";
    private static final String RETROLAMBDA_ACCESSMETHOD_PREFIX = "access$lambda$";

    private static Method GET_MEMBER_REF;
    private static boolean RESOLVE_MEMBER_REF = false;

    static {
        try {
            GET_MEMBER_REF = TypeResolver.class.getDeclaredMethod("getMemberRef", Class.class);
            GET_MEMBER_REF.setAccessible(true);

            RESOLVE_MEMBER_REF = true;
        } catch (Exception ignore) {
        }
    }

    static String resolveMethodRefName(Class<?> lambdaClass) {
        if (Boolean.getBoolean(DISABLE_LAMBDA_METHOD_REF)
                || !RESOLVE_MEMBER_REF
                || !lambdaClass.isSynthetic()) {
            return null;
        }
        Member member;
        try {
            member = (Member) GET_MEMBER_REF.invoke(null, lambdaClass);
        } catch (Exception ignore) {
            return null;
        }
        if (member != null) {
            if (member.isSynthetic()) {
                if (member.getName().startsWith(LAMBDA_METHOD_PREFIX)) {
                    return "`" + methodToString(member) + "`";
                } else if (member.getName().startsWith(RETROLAMBDA_ACCESSMETHOD_PREFIX)) {
                    return "`" + methodToString(member) + "`";
                }
                return null;
            }
            if (member instanceof Constructor
                    || member instanceof Method) {
                return "`" + methodToString(member) + "`";
            }
        }
        return null;
    }

    private static String methodToString(Member methodRef) {
        StringBuilder sb = new StringBuilder();
        if (methodRef instanceof Method) {
            sb.append(((Method) methodRef).getReturnType().getSimpleName());
            sb.append(" ");
        }
        if (methodRef.getName().startsWith(LAMBDA_METHOD_PREFIX)) {
            sb.append(LAMBDA_METHOD_PREFIX);
        } else if (methodRef.getName().startsWith(RETROLAMBDA_ACCESSMETHOD_PREFIX)) {
            sb.append(LAMBDA_METHOD_PREFIX);
        } else if (methodRef instanceof Constructor) {
            sb.append("new ");
            sb.append(methodRef.getDeclaringClass().getSimpleName());
        } else {
            sb.append(methodRef.getDeclaringClass().getSimpleName());
            sb.append(".");
            sb.append(methodRef.getName());
        }
        sb.append("(");
        Class<?>[] parameterTypes;
        if (methodRef instanceof Constructor) {
            parameterTypes = ((Constructor<?>) methodRef).getParameterTypes();
        } else if (methodRef instanceof Method) {
            parameterTypes = ((Method) methodRef).getParameterTypes();
        } else {
            parameterTypes = new Class[0];
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(parameterTypes[i].getSimpleName());
        }
        sb.append(")");
        return sb.toString();
    }
}
