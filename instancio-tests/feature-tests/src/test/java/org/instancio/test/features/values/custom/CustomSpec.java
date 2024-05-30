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
package org.instancio.test.features.values.custom;

import org.instancio.Random;
import org.instancio.generator.ValueSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.support.Global;

class CustomSpec extends AbstractGenerator<String> implements ValueSpec<String> {

    static final int DEFAULT_MIN_LENGTH = 7;
    static final int DEFAULT_MAX_LENGTH = 10;

    private int length;

    CustomSpec() {
        super(Global.generatorContext());

        length = getContext().random().intRange(DEFAULT_MIN_LENGTH, DEFAULT_MAX_LENGTH);
    }

    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    public CustomSpec nullable() {
        super.nullable();
        return this;
    }

    public CustomSpec length(final int length) {
        this.length = length;
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        return random.lowerCaseAlphabetic(length);
    }
}
