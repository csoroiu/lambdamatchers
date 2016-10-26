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

import ro.derbederos.hamcrest.Java8API;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;

import static ro.derbederos.hamcrest.internal.Utils.getRealClassName;
import static ro.derbederos.hamcrest.internal.Utils.isLambdaClass;

// https://github.com/TrigerSoft/jaque/blob/master/src/main/java/com/trigersoft/jaque/expression/ExpressionClassCracker.java
@SuppressWarnings("Since15")
@Java8API
class DumpedLambdaByteCodeMethodRefResolver extends ByteCodeMethodRefResolver {
    private static final String DUMP_PROXY_CLASSES_PATH = "jdk.internal.lambda.dumpProxyClasses";
    private static final File DUMP_FOLDER;

    static {
        String path = AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty(DUMP_PROXY_CLASSES_PATH);
            }
        }, null);
        File localFolder = null;
        if (path != null) {
            try {
                path = path.trim();
                final Path dir = Paths.get(path.length() == 0 ? "." : path);
                boolean localSupport = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                    @Override
                    public Boolean run() {
                        return validateDumpDir(dir);
                    }
                }, null);
                if (localSupport) {
                    localFolder = dir.toFile();
                }
            } catch (InvalidPathException ignore) {
            }
        }
        DUMP_FOLDER = localFolder;
    }

    @Override
    public boolean isAvailable() {
        return DUMP_FOLDER != null;
    }

    @Override
    public boolean supportsClass(Class<?> lambdaClass) {
        return isAvailable() && isLambdaClass(lambdaClass) && getFileForClass(lambdaClass) != null;
    }

    private static boolean validateDumpDir(Path path) {
        return Files.exists(path)
                && Files.isDirectory(path)
                && Files.isWritable(path);
    }

    private static File getFileForClass(Class<?> lambdaClass) {
        String className = getRealClassName(lambdaClass);
        File file = new File(DUMP_FOLDER, className.replace('.', File.separatorChar) + ".class");
        if (file.exists() && file.isFile() && file.canRead()) {
            return file;
        } else {
            return null;
        }
    }

    @Override
    byte[] getByteCodeOf(Class<?> lambdaClass) throws IOException {
        File file = getFileForClass(lambdaClass);
        if (file != null) {
            try (InputStream is = new FileInputStream(file)) {
                return IOUtils.readFully(is, (int) file.length());
            }
        }
        return null;
    }
}
