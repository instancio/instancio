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
package org.instancio.internal.generator.misc;

import org.instancio.Random;
import org.instancio.feed.FeedSpec;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.AbstractGenerator;
import org.jspecify.annotations.Nullable;

public class NullGenerator<T> extends AbstractGenerator<T> implements FeedSpec<T> {

    public NullGenerator(final GeneratorContext context) {
        super(context);
    }

    @Nullable
    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    public NullGenerator<T> nullable() {
        super.nullable();
        return this;
    }

    @Nullable
    @Override
    protected T tryGenerateNonNull(final Random random) {
        return null;
    }
}
