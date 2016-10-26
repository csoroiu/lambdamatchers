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

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public interface MethodRefResolver {
    String LAMBDA_METHOD_PREFIX = "lambda$";
    String RETROLAMBDA_ACCESSMETHOD_PREFIX = "access$lambda$";

    boolean isAvailable();

    boolean supportsClass(Class<?> lambdaClass);

    Member resolveMethodReference(Class<?> functionalInterface, Class<?> lambdaClass);

    default String methodToString(Member methodRef) {
        if (methodRef == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (methodRef.getName().startsWith(LAMBDA_METHOD_PREFIX)
                || methodRef.getName().startsWith(RETROLAMBDA_ACCESSMETHOD_PREFIX)) {
            sb.append("(");
            sb.append(((Method) methodRef).getReturnType().getSimpleName());
            sb.append(")");
        }

        sb.append(methodRef.getDeclaringClass().getSimpleName());
        sb.append("::");
        if (methodRef instanceof Constructor) {
            sb.append("new");
        } else {
            sb.append(methodRef.getName());
        }
        return sb.toString();
    }
}
