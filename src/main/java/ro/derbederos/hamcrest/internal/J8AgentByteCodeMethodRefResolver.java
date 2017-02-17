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

import java.io.IOException;

import static ro.derbederos.hamcrest.internal.Utils.getRealClassName;
import static ro.derbederos.hamcrest.internal.Utils.isLambdaClass;

class J8AgentByteCodeMethodRefResolver extends ByteCodeMethodRefResolver {

    @Override
    public boolean isAvailable() {
        return J8Agent.isSavingAgentLoaded();
    }

    @Override
    public boolean supportsClass(Class<?> lambdaClass) {
        return isAvailable() && isLambdaClass(lambdaClass);
    }

    @Override
    byte[] getByteCodeOf(Class<?> lambdaClass) throws IOException {
        ClassLoader classLoader = lambdaClass.getClassLoader();
        return J8Agent.getLambdaClassByteCode(classLoader, getRealClassName(lambdaClass));
    }
}
