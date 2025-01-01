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
package org.instancio.tests.jpms.spi.impl;

import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.spi.InstancioServiceProvider;

public class JpmsInstancioServiceProviderImpl implements InstancioServiceProvider {

    @Override
    public GeneratorProvider getGeneratorProvider() {
        return (node, gen) -> {
            if (node.getTargetClass() == String.class) {
                return new SpiStringGenerator();
            }
            return null;
        };
    }

    private static class SpiStringGenerator implements Generator<String> {
        @Override
        public String generate(final Random random) {
            return "SPI-string";
        }
    }
}
