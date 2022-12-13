/*
 * Copyright 2022 the original author or authors.
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
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.internal.generator.InternalGeneratorHint;

import java.util.function.Supplier;

public final class SupplierAdapter implements Generator<Object> {

    private static final Hints HINT_POPULATE_ACTION_NONE = Hints.builder()
            .populateAction(PopulateAction.NONE)
            .with(InternalGeneratorHint.builder().excludeFromCallbacks(true).build())
            .build();

    private final Supplier<?> supplier;

    public SupplierAdapter(final Supplier<?> supplier) {
        this.supplier = supplier;
    }


    @Override
    public void init(final GeneratorContext context) {
        // always no-op
    }

    @Override
    public Object generate(final Random random) {
        return supplier.get();
    }

    /**
     * Objects created via {@link Supplier} should not be modified
     * and callbacks should never be called on returned objects.
     *
     * @return hint to not modify the object
     */
    @Override
    public Hints hints() {
        return HINT_POPULATE_ACTION_NONE;
    }

}
