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
package org.instancio.generator.specs;

/**
 * A spec for generating numbers that pass the Mod10 checksum algorithm.
 * This spec supports {@link AsGeneratorSpec}.
 *
 * @since 2.16.0
 */
public interface Mod10AsGeneratorSpec extends Mod10GeneratorSpec, AsGeneratorSpec<String> {

    @Override
    Mod10AsGeneratorSpec length(int length);

    @Override
    Mod10AsGeneratorSpec multiplier(int multiplier);

    @Override
    Mod10AsGeneratorSpec weight(int weight);

    @Override
    Mod10AsGeneratorSpec startIndex(int startIndex);

    @Override
    Mod10AsGeneratorSpec endIndex(int endIndex);

    @Override
    Mod10AsGeneratorSpec checkDigitIndex(int checkDigitIndex);

    @Override
    Mod10AsGeneratorSpec nullable();
}
