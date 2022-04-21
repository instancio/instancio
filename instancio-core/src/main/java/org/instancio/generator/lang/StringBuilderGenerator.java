/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.generator.lang;

import org.instancio.generator.AbstractGenerator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.RandomProvider;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

public class StringBuilderGenerator extends AbstractGenerator<StringBuilder> {

    private final int minLength;
    private final int maxLength;

    public StringBuilderGenerator(final GeneratorContext context) {
        super(context);

        final Settings settings = context.getSettings();
        this.minLength = settings.get(Keys.STRING_MIN_LENGTH);
        this.maxLength = settings.get(Keys.STRING_MAX_LENGTH);
    }

    @Override
    public StringBuilder generate(final RandomProvider random) {
        final int length = random.intRange(minLength, maxLength + 1);
        return new StringBuilder(random.upperCaseAlphabetic(length));
    }
}
