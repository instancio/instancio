/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.test.features.blank;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.instancio.Select.all;
import static org.instancio.Select.types;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.BLANK, Feature.GENERATOR, Feature.SUPPLY})
@ExtendWith(InstancioExtension.class)
class BlankWithCustomGeneratorTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FAIL_ON_ERROR, true);

    private final Generator<StringsDef> pojoGenerator = new Generator<StringsDef>() {
        @Override
        public StringsDef generate(final Random random) {
            return StringsDef.builder()
                    .d("D")
                    .e("E")
                    .build();
        }

        @Override
        public Hints hints() {
            return Hints.afterGenerate(AfterGenerate.POPULATE_ALL);
        }
    };

    @Test
    void withPojoGenerator() {
        final StringsAbc result = Instancio.ofBlank(StringsAbc.class)
                .supply(all(StringsDef.class), pojoGenerator)
                .create();

        // The values assigned to the POJO's fields by the custom generator are overwritten.
        // This is because to create a blank, a predicate selector is used internally
        // to set all leaf nodes to null, which overwrites custom generator values.
        assertThatObject(result).hasAllFieldsOfTypeSetToNull(String.class);
        assertThatObject(result.def).hasAllFieldsOfTypeSetToNull(String.class);
        assertThatObject(result.def.ghi).hasAllFieldsOfTypeSetToNull(String.class);
    }

    @Test
    void withStringGenerator() {
        final Generator<String> generator = random -> "foo";
        final StringsAbc result = Instancio.ofBlank(StringsAbc.class)
                .supply(types().of(String.class), generator)
                .create();

        assertThatObject(result).hasAllFieldsOfTypeEqualTo(String.class, "foo");
        assertThatObject(result.def).hasAllFieldsOfTypeEqualTo(String.class, "foo");
        assertThatObject(result.def.ghi).hasAllFieldsOfTypeEqualTo(String.class, "foo");
    }
}
