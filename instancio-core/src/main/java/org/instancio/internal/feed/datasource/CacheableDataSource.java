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

import org.instancio.feed.DataSource;

import java.io.IOException;
import java.io.InputStream;

public class CacheableDataSource implements CacheableSource {
    private final DataSource delegate;
    private final Object cacheKey;

    public CacheableDataSource(final DataSource delegate, final Object cacheKey) {
        this.delegate = delegate;
        this.cacheKey = cacheKey;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object getKey() {
        return cacheKey;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return delegate.getInputStream();
    }
}
