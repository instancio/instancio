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
package org.instancio.internal;

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generation.GenerationListener;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

class CallbackHandlerTest {

    @Test
    void createShouldReturnNoopClass_whenThereAreNoCallbacks() {
        final ModelContext ctx = ModelContext.builder(String.class).build();
        final GenerationListener result = CallbackHandler.create(ctx);

        assertThat(result).isExactlyInstanceOf(CallbackHandler.NoopCallbackHandler.class);
    }

    @Test
    void createShouldReturnCallbackHandler_whenThereCallbackIsPresent() {
        final ModelContext ctx = ModelContext.builder(String.class)
                .withOnCompleteCallback(allStrings(), System.out::println)
                .build();

        final GenerationListener result = CallbackHandler.create(ctx);

        assertThat(result).isExactlyInstanceOf(CallbackHandler.class);
    }
}
