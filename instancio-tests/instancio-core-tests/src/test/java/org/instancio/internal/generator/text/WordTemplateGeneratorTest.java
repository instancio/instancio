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
package org.instancio.internal.generator.text;

import org.instancio.internal.generator.AbstractGeneratorTestTemplate;

class WordTemplateGeneratorTest extends AbstractGeneratorTestTemplate<String, WordTemplateGenerator> {

    private final WordTemplateGenerator generator = new WordTemplateGenerator(
            getGeneratorContext(), "hello ${noun}");

    @Override
    protected String getApiMethod() {
        return "wordTemplate()";
    }

    @Override
    protected WordTemplateGenerator generator() {
        return generator;
    }
}