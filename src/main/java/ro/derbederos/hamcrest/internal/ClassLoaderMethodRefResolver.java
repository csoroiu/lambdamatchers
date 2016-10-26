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
import java.net.URL;
import java.net.URLConnection;

import static ro.derbederos.hamcrest.internal.IOUtils.readFully;
import static ro.derbederos.hamcrest.internal.Utils.getRealClassName;
import static ro.derbederos.hamcrest.internal.Utils.isRetroLambdaClass;

class ClassLoaderMethodRefResolver extends ByteCodeMethodRefResolver {

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean supportsClass(Class<?> lambdaClass) {
        return isAvailable() && isRetroLambdaClass(lambdaClass);
    }

    @Override
    byte[] getByteCodeOf(Class<?> lambdaClass) throws IOException {
        String name = getRealClassName(lambdaClass);
        int idx = name.lastIndexOf('.');
        final String classFileName = name.substring(idx + 1) + ".class";
        URL url = lambdaClass.getResource(classFileName);
        if (url == null) {
            return null;
        }
        URLConnection urlConnection = url.openConnection();
        int size = urlConnection.getContentLength();
        if (size <= 0) {
            return null;
        } else {
            return readFully(urlConnection.getInputStream(), size);
        }
    }
}
