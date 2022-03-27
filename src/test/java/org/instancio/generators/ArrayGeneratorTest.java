/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.generators;

import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.testsupport.fixtures.Types;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.GeneratedHintsAssert.assertHints;

class ArrayGeneratorTest {

    private static final int MIN_SIZE = 101;
    private static final int MAX_SIZE = 102;

    @Test
    void hints() {
        final Settings set = Settings.defaults()
                .set(Setting.ARRAY_MIN_LENGTH, MIN_SIZE)
                .set(Setting.ARRAY_MAX_LENGTH, MAX_SIZE)
                .set(Setting.ARRAY_NULLABLE, true)
                .set(Setting.ARRAY_ELEMENTS_NULLABLE, true);

        final ModelContext<?> context = ModelContext.builder(Types.STRING.get())
                .withSettings(set)
                .build();

        final ArrayGenerator<?> generator = new ArrayGenerator<>(context, String.class);
        final String[] result = (String[]) generator.generate();
        assertThat(result).hasSizeBetween(MIN_SIZE, MAX_SIZE);

        assertHints(generator.getHints())
                .dataStructureSize(0) // does not set this hint
                .nullableResult(true)
                .nullableElements(true)
                .ignoreChildren(false)
                .nullableMapKeys(false)
                .nullableMapValues(false);
    }
}
