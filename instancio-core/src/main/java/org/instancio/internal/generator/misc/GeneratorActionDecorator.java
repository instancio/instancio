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
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;

final class GeneratorActionDecorator<T> extends GeneratorDecorator<T> {

    private final AfterGenerate defaultAfterGenerate;

    GeneratorActionDecorator(
            final Generator<T> delegate,
            final AfterGenerate defaultAfterGenerate) {

        super(delegate);
        this.defaultAfterGenerate = defaultAfterGenerate;
    }

    @Override
    public Hints hints() {
        final Hints hints = getDelegate().hints();

        if (hints == null) {
            return Hints.afterGenerate(defaultAfterGenerate);
        }

        if (hints.afterGenerate() == null) {
            return Hints.builder(hints)
                    .afterGenerate(defaultAfterGenerate)
                    .build();
        }
        return hints;
    }
}
