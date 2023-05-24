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
 * A spec for generating numbers that pass the Mod11 checksum algorithm.
 * This spec supports {@link AsGeneratorSpec}.
 *
 * @since 2.16.0
 */
public interface Mod11AsGeneratorSpec extends Mod11GeneratorSpec, AsGeneratorSpec<String> {

    @Override
    Mod11AsGeneratorSpec length(int length);

    @Override
    Mod11AsGeneratorSpec threshold(int threshold);

    @Override
    Mod11AsGeneratorSpec startIndex(int startIndex);

    @Override
    Mod11AsGeneratorSpec endIndex(int endIndex);

    @Override
    Mod11AsGeneratorSpec checkDigitIndex(int checkDigitIndex);

    @Override
    Mod11AsGeneratorSpec treatCheck10As(char treatCheck10As);

    @Override
    Mod11AsGeneratorSpec treatCheck11As(char treatCheck11As);

    @Override
    Mod11AsGeneratorSpec leftToRight();

    @Override
    Mod11AsGeneratorSpec nullable();
}
