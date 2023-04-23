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
package org.instancio.internal.generator;

import org.instancio.Random;
import org.instancio.exception.InstancioTerminatingException;
import org.instancio.generator.GeneratorContext;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneratorUtilTest {

    private static class AnInternalGenerator extends AbstractGenerator<String> {

        protected AnInternalGenerator(final GeneratorContext context) {
            super(context);
        }

        @Override
        public String apiMethod() {
            return null;
        }

        @Override
        protected String tryGenerateNonNull(final Random random) {
            return "foo";
        }
    }

    @Test
    void instantiateInternalGenerator() {
        final GeneratorContext context = new GeneratorContext(Settings.create(), new DefaultRandom());

        assertThatThrownBy(() -> GeneratorUtil.instantiateInternalGenerator(AnInternalGenerator.class, context))
                .isExactlyInstanceOf(InstancioTerminatingException.class)
                .hasMessageContaining("Instancio encountered an error.")
                .hasMessageContaining("Please submit a bug report including the stacktrace:")
                .hasMessageContaining("https://github.com/instancio/instancio/issues")
                .hasMessageContaining("-> Error instantiating generator class org.instancio.internal.generator.GeneratorUtilTest$AnInternalGenerator");
    }
}
