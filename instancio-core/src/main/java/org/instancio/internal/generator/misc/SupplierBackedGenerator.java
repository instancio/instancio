/*
 * Copyright 2022-2024 the original author or authors.
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

import java.util.function.Supplier;

/**
 * Slightly different version of {@link SupplierAdapter}.
 * Unlike the latter, this one does not exclude values
 * from {@code onComplete()} callbacks.
 */
public class SupplierBackedGenerator<T> extends AbstractGenerator<T> implements FeedSpec<T> {

    private final Supplier<T> supplier;

    public SupplierBackedGenerator(final GeneratorContext context, final Supplier<T> supplier) {
        super(context);
        this.supplier = supplier;
    }

    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    public SupplierBackedGenerator<T> nullable() {
        super.nullable();
        return this;
    }

    @Override
    public SupplierBackedGenerator<T> nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    @Override
    protected T tryGenerateNonNull(final Random random) {
        return supplier.get();
    }
}
