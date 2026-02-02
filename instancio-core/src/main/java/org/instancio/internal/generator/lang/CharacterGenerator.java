/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.internal.generator.lang;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.CharacterSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.settings.Keys;

public class CharacterGenerator extends AbstractGenerator<Character>
        implements CharacterSpec {

    private char min = 'A';
    private char max = 'Z';

    public CharacterGenerator(final GeneratorContext context) {
        super(context);
        super.nullable(context.getSettings().get(Keys.CHARACTER_NULLABLE));
    }

    @Override
    public String apiMethod() {
        return "chars()";
    }

    @Override
    public CharacterGenerator range(final char min, final char max) {
        ApiValidator.isTrue(min <= max,
                "invalid 'range(%s, %s)': lower bound must be less than or equal to upper bound", min, max);

        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public CharacterGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected Character tryGenerateNonNull(final Random random) {
        return random.characterRange(min, max);
    }
}
