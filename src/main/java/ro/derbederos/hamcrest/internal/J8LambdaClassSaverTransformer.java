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

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ro.derbederos.hamcrest.internal.Utils.isLambdaClass;

class J8LambdaClassSaverTransformer implements ClassFileTransformer {
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, byte[]>> lambdaByteCodeCache = new ConcurrentHashMap<>();

    @Override
    public byte[] transform(final ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {
        ClassReader classReader = new ClassReader(classFileBuffer);
        if (className == null) {
            // Since JDK 8 build b121 or so, lambda classes have a null class name,
            // but we can read it from the bytecode here the name still exists.
            className = classReader.getClassName();
        }
        boolean isLambda = isLambdaClass(className)
                && (Opcodes.ACC_SYNTHETIC & classReader.getAccess()) != 0;
        if (isLambda) {
            storeLambdaClassByteCode(classLoader, className, classFileBuffer);
        }
        return null;
    }

    private void storeLambdaClassByteCode(ClassLoader classLoader, String className, byte[] classFileBuffer) {
        className = className.replace('/', '.');
        Integer classLoaderKey = getClassLoaderKey(classLoader);
        ConcurrentHashMap<String, byte[]> classLoaderCache = lambdaByteCodeCache.get(classLoaderKey);
        if (classLoaderCache == null) {
            classLoaderCache = new ConcurrentHashMap<>();
            ConcurrentHashMap<String, byte[]> oldCache = lambdaByteCodeCache.putIfAbsent(classLoaderKey, classLoaderCache);
            classLoaderCache = (oldCache == null) ? classLoaderCache : oldCache;
        }
        classLoaderCache.putIfAbsent(className, classFileBuffer);
    }

    private static int getClassLoaderKey(ClassLoader classLoader) {
        return System.identityHashCode(classLoader);
    }

    byte[] getLambdaClassByteCode(ClassLoader classLoader, String className) {
        Integer classLoaderKey = getClassLoaderKey(classLoader);
        Map<String, byte[]> classLoaderCache = lambdaByteCodeCache.get(classLoaderKey);
        return (classLoaderCache == null) ? null : classLoaderCache.get(className);
    }
}