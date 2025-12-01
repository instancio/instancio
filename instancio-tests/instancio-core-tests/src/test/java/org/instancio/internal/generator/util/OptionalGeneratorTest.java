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
package org.instancio.internal.generator.util;

import org.instancio.generator.Hints;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.instancio.testsupport.asserts.HintsAssert;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OptionalGeneratorTest extends AbstractGeneratorTestTemplate<Optional<Boolean>, OptionalGenerator<Boolean>> {

    private final OptionalGenerator<Boolean> generator = new OptionalGenerator<>(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "optional()";
    }

    @Override
    protected OptionalGenerator<Boolean> generator() {
        return generator;
    }

    @Override
    @Test
    protected void hints() {
        final Hints hints = generator().hints();

        HintsAssert.assertHints(hints)
                .afterGenerate(null)
                .containerHintGenerateEntriesIsBetween(1, 1);
    }

    @Override
    @Test
    protected void tryGenerateNonNull() {
        final Object result = generator().tryGenerateNonNull(random);

        // Returns null to delegate creation of optional to the engine
        assertThat(result).isNull();
    }
}
