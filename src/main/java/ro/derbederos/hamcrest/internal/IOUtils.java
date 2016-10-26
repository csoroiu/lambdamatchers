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

import java.io.*;

class IOUtils {

    private IOUtils() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    static void copy(InputStream in, OutputStream out) throws IOException {
        copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    static void copy(InputStream in, OutputStream out, int buffSize) throws IOException {
        copy(in, out, false, buffSize);
    }

    static void copy(InputStream in, OutputStream out, boolean autoFlush, int buffSize) throws IOException {
        int i;
        byte[] b = new byte[buffSize];
        while ((i = in.read(b)) >= 0) {
            out.write(b, 0, i);
            if (autoFlush) {
                out.flush();
            }
        }
    }

    static byte[] readFully(InputStream in, int expectedSize) throws IOException {
        ByteArrayOutputStream bis = new ByteArrayOutputStream(expectedSize);
        copy(in, bis);
        if (bis.size() != expectedSize) {
            throw new EOFException("Detect premature EOF");
        }
        return bis.toByteArray();
    }
}
