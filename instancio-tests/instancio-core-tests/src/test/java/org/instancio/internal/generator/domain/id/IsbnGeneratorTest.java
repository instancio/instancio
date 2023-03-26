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
package org.instancio.internal.generator.domain.id;

import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IsbnGeneratorTest extends AbstractGeneratorTestTemplate {

    private final IsbnGenerator generator = new IsbnGenerator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "isbn()";
    }

    @Override
    protected AbstractGenerator<?> generator() {
        return generator;
    }

    @Test
    void generate() {
        final String result = generator.generate(random);
        assertThat(result).hasSize(13);
        // Actual validation is done in Hibernate bean validation tests
    }
}
