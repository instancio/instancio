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

import org.instancio.generator.ValueSpec;

/**
 * A spec for generating numbers that pass the Mod11 checksum algorithm.
 *
 * @since 2.16.0
 */
public interface Mod11Spec extends Mod11GeneratorSpec, ValueSpec<String> {

    @Override
    Mod11Spec length(int length);

    @Override
    Mod11Spec threshold(int threshold);

    @Override
    Mod11Spec startIndex(int startIndex);

    @Override
    Mod11Spec endIndex(int endIndex);

    @Override
    Mod11Spec checkDigitIndex(int checkDigitIndex);

    @Override
    Mod11Spec treatCheck10As(char treatCheck10As);

    @Override
    Mod11Spec treatCheck11As(char treatCheck11As);

    @Override
    Mod11Spec leftToRight();

    @Override
    Mod11Spec nullable();
}
