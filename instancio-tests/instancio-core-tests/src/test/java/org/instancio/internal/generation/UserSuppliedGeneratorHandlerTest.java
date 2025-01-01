/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.generation;

import org.instancio.internal.context.ModelContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

class UserSuppliedGeneratorHandlerTest {

    // required ctor arg but not needed for the test
    private final UserSuppliedGeneratorProcessor generatorProcessor = null;

    @Test
    void createShouldReturnNoopClass_whenModelContextHasNoGenerators() {
        final ModelContext ctx = ModelContext.builder(String.class).build();
        final NodeHandler result = UserSuppliedGeneratorHandler.create(ctx, generatorProcessor);

        assertThat(result).isSameAs(NodeHandler.NOOP_HANDLER);
    }

    @Test
    void shouldReturnGeneratorHandler_whenModelContextHasGenerators() {
        final ModelContext ctx = ModelContext.builder(String.class)
                .withGenerator(allStrings(), random -> "foo")
                .build();

        final NodeHandler result = UserSuppliedGeneratorHandler.create(ctx, generatorProcessor);

        assertThat(result).isExactlyInstanceOf(UserSuppliedGeneratorHandler.class);
    }
}
