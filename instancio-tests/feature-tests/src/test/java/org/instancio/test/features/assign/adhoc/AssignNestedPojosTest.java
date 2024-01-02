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
package org.instancio.test.features.assign.adhoc;

import lombok.Data;
import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

/**
 * Adhoc tests based on the following structure:
 *
 * <pre>
 *         Root
 *         /  \
 *        A    E
 *       /      \
 *      B        F
 *     / \        \
 *   C    D        G
 *                  \
 *                   H
 * </pre>
 */
@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignNestedPojosTest {

    private static final String
            A_VAL = "A",
            B_VAL = "B",
            C_VAL = "C",
            D_VAL = "D",
            E_VAL = "E",
            F_VAL = "F",
            G_VAL = "G",
            H_VAL = "H";

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void whenH_thenC_whenC_thenF_whenF_thenD() {
        final Root result = Instancio.of(Root.class)
                .generate(field(H::getVal), gen -> gen.oneOf(H_VAL, "other"))
                .assign(Assign.given(H::getVal).is(H_VAL).set(field(C::getVal), C_VAL))
                .assign(Assign.given(C::getVal).is(C_VAL).set(field(F::getVal), F_VAL))
                .assign(Assign.given(F::getVal).is(F_VAL).set(field(D::getVal), D_VAL))
                .create();

        if (H_VAL.equals(result.e.f.g.h.val)) {
            assertThat(result.a.b.c.val).isEqualTo(C_VAL);
            assertThat(result.a.b.d.val).isEqualTo(D_VAL);
            assertThat(result.e.f.val).isEqualTo(F_VAL);
        } else {
            assertThat(result.e.f.g.h.val).isEqualTo("other");
            assertThat(result.a.b.c.val).isNotEqualTo(C_VAL);
            assertThat(result.a.b.d.val).isNotEqualTo(D_VAL);
            assertThat(result.e.f.val).isNotEqualTo(F_VAL);
        }
    }

    @Test
    void whenH_thenABCD() {
        final Root result = Instancio.of(Root.class)
                .set(field(H::getVal), H_VAL)
                .assign(Assign.given(H::getVal).is(H_VAL)
                        .set(field(A::getVal), A_VAL)
                        .set(field(B::getVal), B_VAL)
                        .set(field(C::getVal), C_VAL)
                        .set(field(D::getVal), D_VAL))
                .create();

        assertThat(result.a.val).isEqualTo(A_VAL);
        assertThat(result.a.b.val).isEqualTo(B_VAL);
        assertThat(result.a.b.c.val).isEqualTo(C_VAL);
        assertThat(result.a.b.d.val).isEqualTo(D_VAL);
    }

    @Test
    void whenH_thenEFG() {
        final Root result = Instancio.of(Root.class)
                .set(field(H::getVal), H_VAL)
                .assign(Assign.given(H::getVal).is(H_VAL)
                        .set(field(E::getVal), E_VAL)
                        .set(field(F::getVal), F_VAL)
                        .set(field(G::getVal), G_VAL))
                .create();

        assertThat(result.e.val).isEqualTo(E_VAL);
        assertThat(result.e.f.val).isEqualTo(F_VAL);
        assertThat(result.e.f.g.val).isEqualTo(G_VAL);
    }

    @Test
    void whenF_thenBH() {
        final Root result = Instancio.of(Root.class)
                .set(field(F::getVal), F_VAL)
                .assign(Assign.given(F::getVal).is(F_VAL)
                        .set(field(B::getVal), B_VAL)
                        .set(field(H::getVal), H_VAL))
                .create();

        assertThat(result.a.b.val).isEqualTo(B_VAL);
        assertThat(result.e.f.g.h.val).isEqualTo(H_VAL);
    }

    @Test
    void whenH_thenF_whenF_thenD_whenD_thenA() {
        final Root result = Instancio.of(Root.class)
                .set(field(H::getVal), H_VAL)
                .assign(Assign.given(H::getVal).is(H_VAL).set(field(F::getVal), F_VAL))
                .assign(Assign.given(F::getVal).is(F_VAL).set(field(D::getVal), D_VAL))
                .assign(Assign.given(D::getVal).is(D_VAL).set(field(A::getVal), A_VAL))
                .create();

        assertThat(result.a.val).isEqualTo(A_VAL);
        assertThat(result.a.b.d.val).isEqualTo(D_VAL);
        assertThat(result.e.f.val).isEqualTo(F_VAL);
    }

    private static @Data class Root {
        private String val;
        private A a;
        private E e;
    }

    private static @Data class A {
        private String val;
        private B b;
    }

    private static @Data class B {
        private String val;
        private C c;
        private D d;
    }

    private static @Data class C {
        private String val;
    }

    private static @Data class D {
        private String val;
    }

    private static @Data class E {
        private String val;
        private F f;
    }

    private static @Data class F {
        private String val;
        private G g;
    }

    private static @Data class G {
        private String val;
        private H h;
    }

    private static @Data class H {
        private String val;
    }
}
