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
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;

import static ro.derbederos.hamcrest.internal.ASMUtils.isBoxingOrUnboxing;
import static ro.derbederos.hamcrest.internal.Utils.getSingleAbstractMethod;

abstract class ByteCodeMethodRefResolver implements MethodRefResolver {

    @Override
    public Member resolveMethodReference(Class<?> functionalInterface, Class<?> lambdaClass) {
        if (!supportsClass(lambdaClass)) {
            return null;
        }
        try {
            byte[] bytecode = getByteCodeOf(lambdaClass);
            if (bytecode == null) {
                return null;
            }
            Method sam = getSingleAbstractMethod(functionalInterface);
            if (sam == null) {
                return null;
            }
            // http://stackoverflow.com/questions/23861619/how-to-read-lambda-expression-bytecode-using-asm
            return extractMethodReference(sam, lambdaClass, bytecode);
        } catch (IOException | IllegalStateException | VerifyError ignore) {
        }
        return null;
    }

    private static Member extractMethodReference(Method functionMethod, Class<?> lambdaClass, byte[] bytecode) {
        MethodNode methodNode = getFunctionMethodNode(functionMethod, bytecode);
        if (methodNode == null) {
            return null;
        }
        // find the last method call and capture that
        MethodInsnNode result = null;
        for (int i = methodNode.instructions.size() - 1; i >= 0; i--) {
            if (methodNode.instructions.get(i) instanceof MethodInsnNode) {
                MethodInsnNode methodInsnNode = (MethodInsnNode) methodNode.instructions.get(i);
                if (result == null) {
                    result = methodInsnNode;
                } else if (isBoxingOrUnboxing(result, methodInsnNode)) {
                    result = methodInsnNode;
                    break;
                } else {
                    break;
                }
            }
        }
        if (result == null) {
            return null;
        }
        // convert MethodInsnNode to Method
        try {
            ClassLoader lambdaClassLoader = lambdaClass.getClassLoader();
            Class<?> clazz = lambdaClassLoader.loadClass(Type.getObjectType(result.owner).getClassName());
            Class<?>[] parameterTypes = getParameterTypes(result.desc, lambdaClassLoader);
            if (result.name.equals("<init>")) {
                return clazz.getDeclaredConstructor(parameterTypes);
            } else {
                return clazz.getDeclaredMethod(result.name, parameterTypes);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalArgumentException | TypeNotPresentException ignore) {
        }
        return null;
    }

    private static Class<?>[] getParameterTypes(String methodDesc, ClassLoader lambdaClassLoader) throws ClassNotFoundException, IllegalArgumentException {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);
        Class<?>[] result = new Class[argumentTypes.length];
        for (int i = 0; i < argumentTypes.length; i++) {
            result[i] = convertTypeToClass(argumentTypes[i], lambdaClassLoader);
        }
        return result;
    }

    private static Class<?> convertTypeToClass(Type type, ClassLoader classLoader) throws ClassNotFoundException, IllegalArgumentException {
        switch (type.getSort()) {
            case Type.BOOLEAN:
                return Boolean.TYPE;
            case Type.CHAR:
                return Character.TYPE;
            case Type.BYTE:
                return Byte.TYPE;
            case Type.SHORT:
                return Short.TYPE;
            case Type.INT:
                return Integer.TYPE;
            case Type.FLOAT:
                return Float.TYPE;
            case Type.LONG:
                return Long.TYPE;
            case Type.DOUBLE:
                return Double.TYPE;
            case Type.OBJECT:
                return classLoader.loadClass(type.getClassName());
            case Type.ARRAY:
                // recursive call
                Class<?> elementType = convertTypeToClass(type.getElementType(), classLoader);
                for (int i = 0; i < type.getDimensions(); i++) {
                    elementType = Array.newInstance(elementType, 0).getClass();
                }
                return elementType;
        }
        throw new IllegalArgumentException("Bad argument type");
    }

    private static MethodNode getFunctionMethodNode(Method functionMethod, byte[] bytecode) {
        ClassReader classReader = new ClassReader(bytecode);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        @SuppressWarnings("unchecked")
        final List<MethodNode> methods = classNode.methods;
        final String name = functionMethod.getName();
        final String fDesc = Type.getMethodDescriptor(functionMethod);
        for (MethodNode m : methods) {
            if (name.equals(m.name)) {
                if (fDesc.equals(m.desc)) {
                    return m;
                }
            }
        }
        return null;
    }

    abstract byte[] getByteCodeOf(Class<?> lambdaClass) throws IOException;
}
