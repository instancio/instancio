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
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.generator.misc.EmitGenerator.WhenEmptyAction;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmitGeneratorTest {

    private final Random random = new DefaultRandom();

    private final EmitGenerator<Number> generator = new EmitGenerator<>(
            new GeneratorContext(Settings.defaults(), random));

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isNull();
    }

    @Test
    void defaultWhenEmptyAction() {
        assertThat(generator.getWhenEmptyAction()).isEqualTo(WhenEmptyAction.EMIT_RANDOM);
    }

    @Test
    void emitNullIsFalse() {
        final InternalGeneratorHint hint = generator.hints().get(InternalGeneratorHint.class);
        assertThat(hint.emitNull()).isFalse();
    }

    @Test
    void emitNullIsTrue() {
        generator.whenEmptyEmitNull();

        final InternalGeneratorHint hint = generator.hints().get(InternalGeneratorHint.class);
        assertThat(hint.emitNull()).isTrue();
    }

    @Test
    void validation() {
        assertThatThrownBy(() -> generator.items((Iterable<Long>) null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'items' Iterable must not be null");

        assertThatThrownBy(() -> generator.items((Integer[]) null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'items' array must not be null");
    }
}
