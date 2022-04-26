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

import org.instancio.Random;
import org.instancio.generator.AbstractGenerator;
import org.instancio.generator.GeneratedHints;
import org.instancio.generator.GeneratorContext;
import org.instancio.settings.Keys;

public class BooleanGenerator extends AbstractGenerator<Boolean> {

    private boolean nullable;

    public BooleanGenerator(final GeneratorContext context) {
        super(context);
        this.nullable = context.getSettings().get(Keys.BOOLEAN_NULLABLE);
    }

    @Override
    public Boolean generate(final Random random) {
        return random.diceRoll(nullable) ? null : random.trueOrFalse();
    }

    @Override
    public GeneratedHints getHints() {
        return GeneratedHints.builder()
                .nullableResult(nullable)
                .ignoreChildren(true)
                .build();
    }
}
