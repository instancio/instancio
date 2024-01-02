/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.generator.specs.can;

import org.instancio.generator.specs.AsGeneratorSpec;

/**
 * Spec for generating <a href="https://en.wikipedia.org/wiki/Social_insurance_number">
 * Canadian Social Insurance Number</a> (SIN) that supports {@link AsGeneratorSpec}.
 *
 * @since 3.1.0
 */
public interface SinAsGeneratorSpec extends SinGeneratorSpec, AsGeneratorSpec<String> {

    @Override
    SinAsGeneratorSpec permanent();

    @Override
    SinAsGeneratorSpec temporary();

    @Override
    SinAsGeneratorSpec separator(String separator);

    @Override
    SinAsGeneratorSpec nullable();
}
