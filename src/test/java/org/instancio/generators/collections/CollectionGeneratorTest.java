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
package org.instancio.generators.collections;

import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.tags.SettingsTag;
import org.junit.jupiter.api.Test;

import static org.instancio.testsupport.asserts.GeneratedHintsAssert.assertHints;

@SettingsTag
class CollectionGeneratorTest {

    private static final int MIN_SIZE = 101;
    private static final int MAX_SIZE = 102;

    @Test
    void hints() {
        final Settings set = Settings.defaults()
                .set(Setting.COLLECTION_MIN_SIZE, MIN_SIZE)
                .set(Setting.COLLECTION_MAX_SIZE, MAX_SIZE)
                .set(Setting.COLLECTION_NULLABLE, true)
                .set(Setting.COLLECTION_ELEMENTS_NULLABLE, true);

        final ModelContext<?> context = ModelContext.builder(Types.STRING.get())
                .withSettings(set)
                .build();

        final CollectionGenerator<?> generator = new CollectionGenerator<>(context);

        assertHints(generator.getHints())
                .dataStructureSizeBetween(MIN_SIZE, MAX_SIZE)
                .nullableResult(true)
                .nullableElements(true)
                .ignoreChildren(false);
    }

}
