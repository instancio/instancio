/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.generator.util;

import org.instancio.Generator;
import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.util.Verify;

import java.util.Collection;
import java.util.Optional;

public class CollectionGeneratorSpecImpl<T> extends CollectionGenerator<T> {

    private Generator<?> delegate;

    public CollectionGeneratorSpecImpl(final GeneratorContext context) {
        super(context);
        this.type = null; // must be either supplied by user, or obtained from the field declaration
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<T> generate(final Random random) {
        Verify.notNull(delegate, "null delegate");
        return (Collection<T>) delegate.generate(random);
    }

    @Override
    public boolean isDelegating() {
        return true;
    }

    @Override
    public void setDelegate(final Generator<?> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<Class<?>> targetClass() {
        return Optional.ofNullable(type);
    }
}
