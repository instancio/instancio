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
package org.instancio.internal.generator.concurrent.atomic;

import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.instancio.internal.generator.util.concurrent.atomic.AtomicBooleanGenerator;

import java.util.concurrent.atomic.AtomicBoolean;

class AtomicBooleanGeneratorTest extends AbstractGeneratorTestTemplate<AtomicBoolean, AtomicBooleanGenerator> {

    @Override
    protected String getApiMethod() {
        return null;
    }

    @Override
    protected AtomicBooleanGenerator generator() {
        return new AtomicBooleanGenerator(getGeneratorContext());
    }
}
