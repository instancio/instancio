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

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.record.StringsAbcRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignValueOfRecordsTest {

    @Test
    @DisplayName("Without a predicate")
    void unconditional() {
        final StringsAbcRecord result = Instancio.of(StringsAbcRecord.class)
                .assign(Assign.valueOf(StringsAbcRecord::a).set("A"))
                .create();

        assertThat(result.a()).isEqualTo("A");
    }

    @SuppressWarnings("NullAway")
    @Test
    @DisplayName("With null predicate")
    void nullPredicate() {
        final StringsAbcRecord result = Instancio.of(StringsAbcRecord.class)
                .assign(Assign.valueOf(StringsAbcRecord::a).set("A"))
                .assign(Assign.valueOf(StringsAbcRecord::a).to(StringsAbcRecord::b).when(null))
                .create();

        assertThat(result.a())
                .isEqualTo("A")
                .isEqualTo(result.b());
    }

    @Test
    @DisplayName("With true predicate")
    void truePredicate() {
        final StringsAbcRecord result = Instancio.of(StringsAbcRecord.class)
                .assign(Assign.valueOf(StringsAbcRecord::a).set("A"))
                .assign(Assign.valueOf(StringsAbcRecord::a).to(StringsAbcRecord::b).when(o -> true))
                .create();

        assertThat(result.a())
                .isEqualTo("A")
                .isEqualTo(result.b());
    }

    @Test
    @DisplayName("With false predicate")
    void falsePredicate() {
        final StringsAbcRecord result = Instancio.of(StringsAbcRecord.class)
                .assign(Assign.valueOf(StringsAbcRecord::a).set("A"))
                .assign(Assign.valueOf(StringsAbcRecord::a).to(StringsAbcRecord::b).when(o -> false))
                .create();

        assertThat(result.a())
                .isEqualTo("A")
                .isNotEqualTo(result.b());
    }

}
