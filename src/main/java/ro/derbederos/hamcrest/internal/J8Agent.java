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

import java.lang.instrument.Instrumentation;

import static ro.derbederos.hamcrest.internal.Utils.JAVA_VERSION;

public class J8Agent {

    private static final J8LambdaClassSaverTransformer savingAgent = new J8LambdaClassSaverTransformer();
    private static volatile boolean savingAgentLoaded = false;

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        // as of https://bugs.openjdk.java.net/browse/JDK-8145964
        // in jdk 9 the instrumentation of anonymous classes won't be triggered
        // http://mail.openjdk.java.net/pipermail/core-libs-dev/2016-January/038353.html
        // this means that a different mechanics should be used to associate the method reference to the
        // name of the generated class
        if (JAVA_VERSION == 1.8 &&
                !new DumpedLambdaByteCodeMethodRefResolver().isAvailable()) {
            instrumentation.addTransformer(savingAgent);
            savingAgentLoaded = true;
        }
    }

    public static void agentmain(String arg, Instrumentation instrumentation) {
        premain(arg, instrumentation);
    }

    static boolean isSavingAgentLoaded() {
        return savingAgentLoaded;
    }

    static byte[] getLambdaClassByteCode(ClassLoader classLoader, String className) {
        return savingAgent.getLambdaClassByteCode(classLoader, className);
    }
}
