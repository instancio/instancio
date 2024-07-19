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
package org.instancio.internal;

import org.instancio.Instancio;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generation.GenerationListener;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

class SetModelValidatingListenerTest {

    @Test
    void createShouldReturnNoopClass_whenThereIsNoSetModel() {
        final ModelContext<Object> ctx = ModelContext.builder(String.class).build();
        final GenerationListener result = SetModelValidatingListener.create(ctx);

        assertThat(result).isSameAs(GenerationListener.NOOP_LISTENER);
    }

    @Test
    void createShouldReturnSetModelValidatingListener_whenSetModelIsPresent() {
        final ModelContext<Object> ctx = ModelContext.builder(String.class)
                .setModel(allStrings(), Instancio.of(String.class).toModel())
                .build();

        final GenerationListener result = SetModelValidatingListener.create(ctx);

        assertThat(result).isExactlyInstanceOf(SetModelValidatingListener.class);
    }
}
