/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.java16.assign;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

/**
 * Adhoc tests based on the following record structure:
 *
 * <pre>
 *           Root
 *        / / | \ \
 *       A B  C  D E
 * </pre>
 */
@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignFlatRecordsTest {

    private static final String
            A_VAL = "A",
            B_VAL = "B",
            C_VAL = "C",
            D_VAL = "D",
            E_VAL = "E";

    @Test
    void whenC_thenA_whenA_thenE_whenE_thenB() {
        final Root result = Instancio.of(Root.class)
                .set(field(C::val), C_VAL)
                .assign(Assign.given(C::val).is(C_VAL).set(field(A::val), A_VAL))
                .assign(Assign.given(A::val).is(A_VAL).set(field(E::val), E_VAL))
                .assign(Assign.given(E::val).is(E_VAL).set(field(B::val), B_VAL))
                .create();

        assertThat(result.a.val).isEqualTo(A_VAL);
        assertThat(result.b.val).isEqualTo(B_VAL);
        assertThat(result.c.val).isEqualTo(C_VAL);
        assertThat(result.d.val).as("random value").hasSizeGreaterThan(1);
        assertThat(result.e.val).isEqualTo(E_VAL);
    }

    @Test
    void whenE_thenD_whenD_thenC_whenC_thenB_whenB_thenA() {
        final Root result = Instancio.of(Root.class)
                .set(field(E::val), E_VAL)
                .assign(Assign.given(E::val).is(E_VAL).set(field(D::val), D_VAL))
                .assign(Assign.given(D::val).is(D_VAL).set(field(C::val), C_VAL))
                .assign(Assign.given(C::val).is(C_VAL).set(field(B::val), B_VAL))
                .assign(Assign.given(B::val).is(B_VAL).set(field(A::val), A_VAL))
                .create();

        assertThat(result.a.val).isEqualTo(A_VAL);
        assertThat(result.b.val).isEqualTo(B_VAL);
        assertThat(result.c.val).isEqualTo(C_VAL);
        assertThat(result.d.val).isEqualTo(D_VAL);
        assertThat(result.e.val).isEqualTo(E_VAL);
    }


    // @formatter:off
    private record Root(A a, B b, C c, D d, E e) {}
    private record A(String val) {}
    private record B(String val) {}
    private record C(String val) {}
    private record D(String val) {}
    private record E(String val) {}
    // @formatter:on
}
