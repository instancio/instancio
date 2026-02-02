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
package org.instancio.generators;

import org.instancio.generator.specs.LuhnGeneratorSpec;
import org.instancio.generator.specs.Mod10GeneratorSpec;
import org.instancio.generator.specs.Mod11GeneratorSpec;

/**
 * Contains built-in generators for checksum-valid numbers.
 *
 * @since 2.16.0
 */
public interface ChecksumGenerators {

    /**
     * Generates numbers that pass the Luhn checksum algorithm.
     *
     * @return API builder reference
     * @since 3.1.0
     */
    LuhnGeneratorSpec luhn();

    /**
     * Generates numbers that pass the Mod10 checksum algorithm.
     *
     * @return API builder reference
     * @since 2.16.0
     */
    Mod10GeneratorSpec mod10();

    /**
     * Generates numbers that pass the Mod11 checksum algorithm.
     *
     * @return API builder reference
     * @since 2.16.0
     */
    Mod11GeneratorSpec mod11();
}
