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

package org.instancio.generators;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.Mod10AsGeneratorSpec;
import org.instancio.generator.specs.Mod11AsGeneratorSpec;
import org.instancio.internal.generator.checksum.Mod10Generator;
import org.instancio.internal.generator.checksum.Mod11Generator;

/**
 * Contains built-in generators for checksum-valid numbers.
 *
 * @since 2.16.0
 */
public class ChecksumGenerators {

    private final GeneratorContext context;

    public ChecksumGenerators(final GeneratorContext context) {
        this.context = context;
    }

    /**
     * Generates numbers that pass the Mod10 checksum algorithm.
     *
     * @return API builder reference
     * @since 2.16.0
     */
    public Mod10AsGeneratorSpec mod10() {
        return new Mod10Generator(context);
    }

    /**
     * Generates numbers that pass the Mod11 checksum algorithm.
     *
     * @return API builder reference
     * @since 2.16.0
     */
    public Mod11AsGeneratorSpec mod11() {
        return new Mod11Generator(context);
    }
}
