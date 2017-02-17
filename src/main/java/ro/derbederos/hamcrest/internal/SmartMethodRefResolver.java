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

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import static ro.derbederos.hamcrest.internal.Utils.JAVA_VERSION;

class SmartMethodRefResolver implements MethodRefResolver {

    private final List<MethodRefResolver> resolvers = new ArrayList<>();

    SmartMethodRefResolver() {
        resolvers.add(new ClassLoaderMethodRefResolver());
        if (JAVA_VERSION == 1.8) {
            resolvers.add(new J8AgentByteCodeMethodRefResolver());
        }
        if (JAVA_VERSION >= 1.8) {
            resolvers.add(new DumpedLambdaByteCodeMethodRefResolver());
            resolvers.add(new SerializableMethodRefResolver());
        }
        resolvers.add(new ConstantPoolMethodRefResolver());
        resolvers.add(NullMethodRefResolver.INSTANCE);
    }

    @Override
    public boolean isAvailable() {
        for (MethodRefResolver methodRefResolver : resolvers) {
            if (methodRefResolver.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supportsClass(Class<?> lambdaClass) {
        for (MethodRefResolver methodRefResolver : resolvers) {
            if (methodRefResolver.supportsClass(lambdaClass)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Member resolveMethodReference(Class<?> functionalInterface, Class<?> lambdaClass) {
        for (MethodRefResolver methodRefResolver : resolvers) {
            Member result = methodRefResolver.resolveMethodReference(functionalInterface, lambdaClass);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
