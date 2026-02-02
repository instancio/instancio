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
package org.instancio.generator.specs;

/**
 * Generator spec for characters.
 *
 * @since 2.0.0
 */
public interface CharacterGeneratorSpec extends AsGeneratorSpec<Character> {

    /**
     * Range of characters to generate.
     *
     * @param min lower bound (inclusive)
     * @param max upper bound (inclusive)
     * @return spec builder
     * @since 2.8.0
     */
    CharacterGeneratorSpec range(char min, char max);

    /**
     * Indicates that {@code null} value can be generated.
     *
     * @return spec builder
     * @since 2.0.0
     */
    CharacterGeneratorSpec nullable();
}
