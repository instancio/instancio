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
 * A spec for generating numbers that pass the Luhn checksum algorithm.
 * This spec supports {@link AsGeneratorSpec}.
 *
 * @since 3.1.0
 */
public interface LuhnAsGeneratorSpec extends LuhnGeneratorSpec, AsGeneratorSpec<String> {

    @Override
    LuhnAsGeneratorSpec length(int length);

    @Override
    LuhnAsGeneratorSpec length(int min, int max);

    @Override
    LuhnAsGeneratorSpec startIndex(int startIndex);

    @Override
    LuhnAsGeneratorSpec endIndex(int endIndex);

    @Override
    LuhnAsGeneratorSpec checkDigitIndex(int checkDigitIndex);

    @Override
    LuhnAsGeneratorSpec nullable();
}
