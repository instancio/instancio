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
 * Spec for generating {@link String} values.
 *
 * @since 2.6.0
 */
public interface StringSpec extends ValueSpec<String>, StringGeneratorSpec {

    @Override
    StringSpec prefix(String prefix);

    @Override
    StringSpec suffix(String suffix);

    @Override
    StringSpec nullable();

    @Override
    StringSpec allowEmpty();

    @Override
    StringSpec allowEmpty(boolean isAllowed);

    @Override
    StringSpec length(int length);

    @Override
    StringSpec length(int minLength, int maxLength);

    @Override
    StringSpec minLength(int length);

    @Override
    StringSpec maxLength(int length);

    @Override
    StringSpec lowerCase();

    @Override
    StringSpec upperCase();

    @Override
    StringSpec mixedCase();

    @Override
    StringSpec alphaNumeric();

    @Override
    StringSpec digits();
}
