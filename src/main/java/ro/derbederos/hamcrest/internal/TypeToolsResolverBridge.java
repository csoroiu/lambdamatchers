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

import _shaded.net.jodah.typetools.TypeResolver;

import java.lang.reflect.Member;

public class TypeToolsResolverBridge implements MethodRefResolver, _shaded.net.jodah.typetools.MethodRefResolver {
    private final MethodRefResolver delegate;

    public static final MethodRefResolver INSTANCE;

    static {
        TypeToolsResolverBridge resolver = new TypeToolsResolverBridge(new SmartMethodRefResolver());
        if (!resolver.isAvailable()) {
            System.err.println("Lambda MethodRefResolver is unavailable");
        }
        TypeResolver.setLambdaResolver(resolver);
        INSTANCE = resolver;
    }


    private TypeToolsResolverBridge(MethodRefResolver delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isAvailable() {
        return delegate.isAvailable();
    }

    @Override
    public boolean supportsClass(Class<?> lambdaClass) {
        return delegate.supportsClass(lambdaClass);
    }

    @Override
    public Member resolveMethodReference(Class<?> functionalInterface, Class<?> lambdaClass) {
        return delegate.resolveMethodReference(functionalInterface, lambdaClass);
    }

    @Override
    public String methodToString(Member methodRef) {
        return delegate.methodToString(methodRef);
    }
}