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
package org.instancio.test.features.assign;

import lombok.Data;
import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.root;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.ASSIGN, Feature.GENERATOR})
@ExtendWith(InstancioExtension.class)
class AssignWithGeneratorHintTest {

    @Data
    private static class DefHolder {
        private StringsDef def1;
        private StringsDef def2;
    }

    private static Generator<StringsDef> generator(final AfterGenerate afterGenerate) {
        return new Generator<StringsDef>() {
            @Override
            public StringsDef generate(final Random random) {
                return new StringsDef();
            }

            @Override
            public Hints hints() {
                return Hints.afterGenerate(afterGenerate);
            }
        };
    }

    @Nested
    class GivenWithHintTest {
        @Test
        void doNotModify() {
            final StringsAbc result = Instancio.of(StringsAbc.class)
                    .assign(Assign.given(root()).satisfies(a -> true)
                            .supply(all(StringsDef.class), generator(AfterGenerate.DO_NOT_MODIFY)))
                    .create();

            assertThatObject(result.def).hasAllFieldsOfTypeSetToNull(String.class);
        }

        @Test
        void populateAll() {
            final StringsAbc result = Instancio.of(StringsAbc.class)
                    .assign(Assign.given(root()).satisfies(a -> true)
                            .supply(all(StringsDef.class), generator(AfterGenerate.POPULATE_ALL)))
                    .create();

            assertThatObject(result.def).isFullyPopulated();
        }
    }

    @Nested
    class ValueOfWithHintTest {
        @Test
        void doNotModify() {
            final DefHolder result = Instancio.of(DefHolder.class)
                    .supply(field(DefHolder::getDef1), generator(AfterGenerate.DO_NOT_MODIFY))
                    .assign(Assign.valueOf(DefHolder::getDef1).to(DefHolder::getDef2))
                    .create();

            assertThatObject(result.def2)
                    .hasAllFieldsOfTypeSetToNull(String.class)
                    .isSameAs(result.def1);
        }

        @Test
        void populateAll() {
            final DefHolder result = Instancio.of(DefHolder.class)
                    .supply(field(DefHolder::getDef1), generator(AfterGenerate.POPULATE_ALL))
                    .assign(Assign.valueOf(DefHolder::getDef1).to(DefHolder::getDef2))
                    .create();

            assertThatObject(result.def2)
                    .isFullyPopulated()
                    .isSameAs(result.def1);
        }
    }
}
