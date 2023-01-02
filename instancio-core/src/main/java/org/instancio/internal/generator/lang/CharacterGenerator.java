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
package org.instancio.internal.generator.lang;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.CharacterGeneratorSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.settings.Keys;

public class CharacterGenerator extends AbstractGenerator<Character> implements CharacterGeneratorSpec {

    private boolean nullable;

    public CharacterGenerator(final GeneratorContext context) {
        super(context);
        this.nullable = context.getSettings().get(Keys.CHARACTER_NULLABLE);
    }

    @Override
    public String apiMethod() {
        return "chars()";
    }

    @Override
    public CharacterGeneratorSpec nullable() {
        this.nullable = true;
        return this;
    }

    @Override
    public Character generate(final Random random) {
        return random.diceRoll(nullable) ? null : random.upperCaseCharacter();
    }
}
