/*
 * Copyright 2022-2023 the original author or authors.
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
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class GeneratorHintsDecoratorTest {
    private static final Hints HINTS = Hints.afterGenerate(AfterGenerate.DO_NOT_MODIFY);
    private static final GeneratorContext CONTEXT = new GeneratorContext(
            Settings.create(), new DefaultRandom());

    @Test
    void decorate() {
        final Generator<?> generator = Mockito.mock(Generator.class);
        final GeneratorHintsDecorator<?> decorator = new GeneratorHintsDecorator<>(generator, HINTS);

        decorator.init(CONTEXT);

        verify(generator).init(CONTEXT);
        assertThat(decorator.getDelegate()).isSameAs(generator);
        assertThat(decorator.hints()).isSameAs(HINTS);
    }
}
