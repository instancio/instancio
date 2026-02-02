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

import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.InternalGeneratorHint;

import java.util.function.Supplier;

public final class SupplierAdapter<T> extends GeneratorDecorator<T> {

    private static final Hints HINT_READ_ONLY_NO_CALLBACKS = Hints.builder()
            .afterGenerate(AfterGenerate.DO_NOT_MODIFY)
            .with(InternalGeneratorHint.builder().excludeFromCallbacks(true).build())
            .build();

    SupplierAdapter(final Supplier<T> supplier) {
        super(random -> supplier.get());
    }

    /**
     * Objects created via {@link Supplier} should not be modified
     * and callbacks should never be called on returned objects.
     *
     * @return hint to not modify the object
     */
    @Override
    public Hints hints() {
        return HINT_READ_ONLY_NO_CALLBACKS;
    }
}
