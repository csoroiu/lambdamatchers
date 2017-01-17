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

import org.objectweb.asm.*;

import java.util.HashSet;
import java.util.Set;

class InvokeDynamicClassVisitor extends ClassVisitor {
    private static final String LAMBDA_METAFACTORY = "java/lang/invoke/LambdaMetafactory";

    private String className;
    private int lineNumber = -1;

    public InvokeDynamicClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    public InvokeDynamicClassVisitor(int api) {
        super(api);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (isLambdaDeserializationHook(access, name, desc)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        String methodName = name;
        lineNumber = -1;

        return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
            public Label label;

            @Override
            public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
                super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
                visitBootstrapMethod(bsm);
                if (bsm.getOwner().equals(LAMBDA_METAFACTORY)) {
                    try {
                        storeLambda(className, methodName, name, Type.getType(desc), bsm, bsmArgs);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }

            Set<Label> exceptionHandlers = new HashSet<>();

            @Override
            public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
                super.visitTryCatchBlock(start, end, handler, type);
                if (type != null) { //skip finally block
                    // validate the type of exception
                    exceptionHandlers.add(handler);
                }
            }

            @Override
            public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
                if (exceptionHandlers.contains(label)) {
                    for (int i = 0; i < nStack; i++) {
                        System.out.println("Linenumber: " + lineNumber);
                        if (stack[i] instanceof String) {
                            System.out.println(" " + stack[i]);
                        }
                    }
                }
                super.visitFrame(type, nLocal, local, nStack, stack);
            }

            @Override
            public void visitLabel(Label label) {
                super.visitLabel(label);
                this.label = label;
            }

            @Override
            public void visitLineNumber(int line, Label start) {
                lineNumber = line;
                super.visitLineNumber(line, start);
            }
        };
    }

    private void storeLambda(String invokerClassName, String invokerMethodName, String invokedName, Type invokedType, Handle bsm, Object[] bsmArgs) throws Throwable {
        if (bsm.getName().equals("metafactory")
                || bsm.getName().equals("altMetafactory")) {
            System.out.println("Linenumber: " + lineNumber);
            Handle methodHandle = (Handle) bsmArgs[1];
            String methodHandleOwner = methodHandle.getOwner();
            String methodHandleDesc = methodHandle.getName() + methodHandle.getDesc();
            System.out.println("MethodHandle called from " + invokerClassName + "#" + invokerMethodName);
            System.out.println("  " + methodHandleOwner + "::" + methodHandleDesc);
        }
    }

    private void visitBootstrapMethod(Handle bsm) {
        visitMethodSignature(bsm.getOwner(), bsm.getName(), bsm.getDesc());
    }

    private void visitMethodSignature(String owner, String name, String desc) {
//        Type methodType = Type.getType(desc);
//        Type returnType = methodType.getReturnType();
//        if (returnType.getSort() != Type.OBJECT
//                || !returnType.getClassName().equals("java.lang.invoke.CallSite")) {
//            System.out.println("Return Type (" +returnType + ") must be a java.lang.invoke.CallSite");
//        }
        System.out.println("Bootstrap " + owner + "#" + name + desc);

    }

    private static boolean hasFlag(int subject, int flag) {
        return (subject & flag) == flag;
    }

    private static boolean isLambdaDeserializationHook(int access, String name, String desc) {
        return name.equals("$deserializeLambda$")
                && desc.equals("(Ljava/lang/invoke/SerializedLambda;)Ljava/lang/Object;")
                && hasFlag(access, Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC);
    }
}
