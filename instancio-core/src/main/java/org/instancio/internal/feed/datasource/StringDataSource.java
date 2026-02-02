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
package org.instancio.internal.feed.datasource;

import org.instancio.documentation.InternalApi;
import org.instancio.feed.DataSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@InternalApi
public class StringDataSource implements DataSource {
    private final byte[] data;

    public StringDataSource(final String s) {
        this.data = s.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(data);
    }
}
