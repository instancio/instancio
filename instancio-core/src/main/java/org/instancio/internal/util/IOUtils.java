/*
 * Copyright 2022-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class IOUtils {
    private IOUtils() {
        // non-instantiable
    }

    public static void writeTo(final Path path, final InputStream inputStream) throws IOException {
        Verify.notNull(inputStream, "Input stream is null");
        try (InputStream in = inputStream;
             OutputStream out = Files.newOutputStream(path)) {
            final byte[] buf = new byte[512];
            for (int read; (read = in.read(buf)) != -1; ) { // NOPMD
                out.write(buf, 0, read);
            }
        }
    }
}
