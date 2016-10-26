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
import org.objectweb.asm.tree.MethodInsnNode;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class ASMUtils {

    private ASMUtils() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * This method returns the method which the {@code methodName} delegates to.
     * In case a delegate cannot be detected, it returns null.
     * // FIXME: 13.11.2016 bellow paragraph needs review
     * A method's body which delegate to another method contains:
     * 1. instructions that load the values of members of parameters on the stack
     * 2. instruction to create an array and store values in case the callee is a varargs method
     * 3. the actual method call
     * 4. type casts between primitive types
     * 5. boxing operation
     * 6. other type casts
     * optionally have a boxing/unboxing method call and return the top of the stack.
     *
     * @param classByteCode the bytecode of the target class
     * @param methodName    the name of the method which delegates the call to another method
     * @param methodDesc    the description (type and arguments) of the method which delegates the call to another method
     * @return the delegate method, or null otherwise
     */
    static Method getMethodDelegate(byte[] classByteCode, String methodName, String methodDesc) {
        // the visited method should only contain at most 2 method calls
        // the method reference and the boxing/unboxing
        // also the load of the parameters
        // TODO in the future i expect to have partial functions support
        return null;
    }

    static boolean isBoxingOrUnboxing(MethodInsnNode currentMethod, MethodInsnNode previousMethod) {
        Type returnType = Type.getReturnType(previousMethod.desc);
        if (isBoxingValueOf(currentMethod)) {
            Type[] argumentTypes = Type.getArgumentTypes(currentMethod.desc);
            if (returnType.getSort() > Type.DOUBLE) {
                return false;
            }
            // FIXME: 16.11.2016 there can be conversions inbetween
            // I2L, I2F, I2D / L2I, L2F, L2D / F2I, F2L, F2D / D2I, D2L, D2F
            return getComputationalType(argumentTypes[0]).equals(getComputationalType(returnType));
        }
        if (isUnboxingXxxValue(currentMethod)) {
            // TODO: 06.11.2016 check if owner is java.lang.Number ?
            // TODO: 06.11.2016 Also check in case of byte and short if intValue is called
            // First conversion instruction: I2L, I2F, I2D / L2I, L2F, L2D / F2I, F2L, F2D / D2I, D2L, D2F or I2B, I2S
            // Second conversion instruction if returtype is byte/char or short: I2B, I2S
            //   valid when previous conversion instruction is L2I, F2I or D2I
            String returnTypeClass = returnType.getInternalName();
            return currentMethod.owner.equals(returnTypeClass);
        }
        return false;
    }

    private static Type getComputationalType(Type argumentType) {
        // types and the java virtual machine
        // types and the java virtual machine
        // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-2.html#jvms-2.11.1
        switch (argumentType.getSort()) {
            case Type.BOOLEAN:
            case Type.BYTE:
            case Type.CHAR:
            case Type.SHORT:
            case Type.INT:
                return Type.INT_TYPE;
            default:
                return argumentType;
        }
    }

    private static boolean isBoxingValueOf(MethodInsnNode method) {
        Type[] argumentTypes = Type.getArgumentTypes(method.desc);
        String returnType = Type.getReturnType(method.desc).getInternalName();
        return "valueOf".equals(method.name) && argumentTypes.length == 1
                && argumentTypes[0].getSort() <= Type.DOUBLE
                && method.owner.equals(boxedType(argumentTypes[0].getClassName()))
                && method.owner.equals(returnType);
    }

    private static boolean isUnboxingXxxValue(MethodInsnNode method) {
        Type[] argumentTypes = Type.getArgumentTypes(method.desc);
        Type returnType = Type.getReturnType(method.desc);
        String methodName = method.name;
        return returnType.getSort() <= Type.DOUBLE && argumentTypes.length == 0
                && methodName.equals(returnType.getClassName() + "Value")
                && (method.owner.equals(boxedType(returnType.getClassName())) ||
                method.owner.equals("java/lang/Number"));
    }

    private static final Map<String, String> PRIMITIVE_WRAPPERS;

    static {
        Map<String, String> types = new HashMap<>();
        types.put("boolean", "java/lang/Boolean");
        types.put("byte", "java/lang/Byte");
        types.put("char", "java/lang/Character");
        types.put("short", "java/lang/Short");
        types.put("int", "java/lang/Integer");
        types.put("long", "java/lang/Long");
        types.put("float", "java/lang/Float");
        types.put("double", "java/lang/Double");
        PRIMITIVE_WRAPPERS = Collections.unmodifiableMap(types);
    }

    private static String boxedType(String primitiveType) {
        return PRIMITIVE_WRAPPERS.get(primitiveType);
    }

    static int readBytecodeVersion(ClassReader classReader) {
        return classReader.readShort(classReader.getItem(1) - 5);
    }
}
