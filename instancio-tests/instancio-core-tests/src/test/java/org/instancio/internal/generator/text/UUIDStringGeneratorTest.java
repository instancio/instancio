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
package org.instancio.internal.generator.text;

import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UUIDStringGeneratorTest extends AbstractGeneratorTestTemplate<String, UUIDStringGenerator> {

    private final UUIDStringGenerator generator = new UUIDStringGenerator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "uuid()";
    }

    @Override
    protected UUIDStringGenerator generator() {
        return generator;
    }

    @Test
    void defaultUUIDString() {
        assertThat(generator.generate(random)).matches("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$");
    }

    @Test
    void upperCase() {
        generator.upperCase();
        assertThat(generator.generate(random)).matches("^[A-F0-9]{8}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{4}-[A-F0-9]{12}$");
    }

    @Test
    void noDashes() {
        generator.withoutDashes();
        assertThat(generator.generate(random)).matches("^[a-f0-9]{32}$");
    }

    @Test
    void upperCaseNoDashes() {
        generator.upperCase().withoutDashes();
        assertThat(generator.generate(random)).matches("^[A-F0-9]{32}$");
    }
}
