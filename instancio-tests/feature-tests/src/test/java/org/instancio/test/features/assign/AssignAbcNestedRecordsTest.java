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
package org.instancio.test.features.assign;

import org.instancio.Assignment;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Selector;
import org.instancio.internal.util.ArrayUtils;
import org.instancio.support.DefaultRandom;
import org.instancio.test.support.record.StringsAbcRecord;
import org.instancio.test.support.record.StringsDefRecord;
import org.instancio.test.support.record.StringsGhiRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.given;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag(Feature.ASSIGN)
class AssignAbcNestedRecordsTest {

    private static final String A_VAL = "A";
    private static final String B_VAL = "B";
    private static final String C_VAL = "C";
    private static final String D_VAL = "D";
    private static final String E_VAL = "E";
    private static final String F_VAL = "F";
    private static final String G_VAL = "G";
    private static final String H_VAL = "H";
    private static final String I_VAL = "I";

    private static final Selector A = field(StringsAbcRecord::a);
    private static final Selector B = field(StringsAbcRecord::b);
    private static final Selector C = field(StringsAbcRecord::c);
    private static final Selector D = field(StringsDefRecord::d);
    private static final Selector E = field(StringsDefRecord::e);
    private static final Selector F = field(StringsDefRecord::f);
    private static final Selector G = field(StringsGhiRecord::g);
    private static final Selector H = field(StringsGhiRecord::h);
    private static final Selector I = field(StringsGhiRecord::i);

    private final Model<StringsAbcRecord> baseModel = Instancio.of(StringsAbcRecord.class)
            .set(A, A_VAL)
            .toModel();

    @Test
    void assignments() {
        final Assignment[] assignments = shuffleArgs(
                valueOf(A).to(B),
                valueOf(A).to(I),
                valueOf(B).to(C),
                valueOf(B).to(D),
                valueOf(B).to(E),
                valueOf(C).to(F),
                valueOf(E).to(H),
                valueOf(I).to(G));

        final StringsAbcRecord result = Instancio.of(baseModel)
                .assign(assignments)
                .create();

        assertThatObject(result).hasAllFieldsOfTypeEqualTo(String.class, A_VAL);
        assertThatObject(result.def()).hasAllFieldsOfTypeEqualTo(String.class, A_VAL);
        assertThatObject(result.def().ghi()).hasAllFieldsOfTypeEqualTo(String.class, A_VAL);
    }

    @Test
    void conditionalAssignments() {
        final Assignment[] assignments = shuffleArgs(
                given(A).is(A_VAL).set(B, B_VAL),
                given(A).is(A_VAL).set(I, I_VAL),
                given(B).is(B_VAL).set(C, C_VAL),
                given(B).is(B_VAL).set(D, D_VAL),
                given(B).is(B_VAL).set(E, E_VAL),
                given(C).is(C_VAL).set(F, F_VAL),
                given(E).is(E_VAL).set(H, H_VAL),
                given(I).is(I_VAL).set(G, G_VAL));

        final StringsAbcRecord result = Instancio.of(baseModel)
                .assign(assignments)
                .create();

        assertThat(result.a()).isEqualTo(A_VAL);
        assertThat(result.b()).isEqualTo(B_VAL);
        assertThat(result.c()).isEqualTo(C_VAL);
        assertThat(result.def().d()).isEqualTo(D_VAL);
        assertThat(result.def().e()).isEqualTo(E_VAL);
        assertThat(result.def().f()).isEqualTo(F_VAL);
        assertThat(result.def().ghi().g()).isEqualTo(G_VAL);
        assertThat(result.def().ghi().h()).isEqualTo(H_VAL);
        assertThat(result.def().ghi().i()).isEqualTo(I_VAL);
    }

    private static Assignment[] shuffleArgs(final Assignment... assignments) {
        ArrayUtils.shuffle(assignments, new DefaultRandom());
        return assignments;
    }
}
