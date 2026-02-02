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
package org.instancio.test.features.assign;

import lombok.Data;
import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.root;
import static org.instancio.Select.scope;

@SuppressWarnings("SameNameButDifferent")
@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignBackReferenceRecordTest {

    @Nested
    class RootRecordTest {
        record Rec(Pojo pojo) {}

        static @Data class Pojo {
            Rec rec;
        }

        @Test
        void rootRecord() {
            final Rec result = Instancio.of(Rec.class)
                    .assign(Assign.valueOf(root()).to(field(Pojo::getRec)))
                    .create();

            assertThat(result.pojo.rec).isSameAs(result);
        }

        @Test
        void rootPojo() {
            final Pojo result = Instancio.of(Pojo.class)
                    .assign(Assign.valueOf(root()).to(field(Rec::pojo)))
                    .create();

            assertThat(result.rec.pojo).isSameAs(result);
        }
    }

    @Nested
    class RootRecordWithListTest {
        record Rec(Pojo pojo) {}

        static class Pojo {
            List<Rec> records;
        }

        @Test
        void rootRecord() {
            final Rec result = Instancio.of(Rec.class)
                    .assign(Assign.valueOf(root()).to(all(Rec.class).within(scope(List.class))))
                    .create();

            assertThat(result.pojo.records)
                    .isNotEmpty()
                    .allSatisfy(element -> assertThat(element).isSameAs(result));
        }
    }

    @Nested
    class RootPojoWithListTest {
        static class Pojo {
            Rec rec;
        }

        record Rec(List<Pojo> pojos) {}

        @Test
        void rootRecord() {
            final Pojo result = Instancio.of(Pojo.class)
                    .assign(Assign.valueOf(root()).to(all(Pojo.class).within(scope(List.class))))
                    .create();

            assertThat(result.rec.pojos)
                    .isNotEmpty()
                    .allSatisfy(element -> assertThat(element).isSameAs(result));
        }
    }


}
