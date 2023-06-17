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
package org.instancio.test.java16.assign;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

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
class AssignNestedRecordsTest {

    private static final String
            A_VAL = "A",
            B_VAL = "B",
            C_VAL = "C",
            D_VAL = "D",
            E_VAL = "E",
            F_VAL = "F",
            G_VAL = "G",
            H_VAL = "H",
            OTHER_VAL = "other-val";

    @Nested
    class WhenHThenAbcd {
        private final Model<Root> model = Instancio.of(Root.class)
                .generate(field(H::val), gen -> gen.oneOf(H_VAL, OTHER_VAL))
                .assign(Assign.given(H::val).is(H_VAL)
                        .set(field(A::val), A_VAL)
                        .set(field(B::val), B_VAL)
                        .set(field(C::val), C_VAL)
                        .set(field(D::val), D_VAL))
                .toModel();

        @Test
        void create() {
            final Root result = Instancio.create(model);

            assertResult(result);
        }

        @Test
        void createList() {
            final List<Root> results = Instancio.ofList(model).create();

            assertThat(results)
                    .isNotEmpty()
                    .allSatisfy(this::assertResult);
        }

        private void assertResult(final Root result) {
            if (result.e.f.g.h.val.equals(H_VAL)) {
                assertThat(result.a.val).isEqualTo(A_VAL);
                assertThat(result.a.b.val).isEqualTo(B_VAL);
                assertThat(result.a.b.c.val).isEqualTo(C_VAL);
                assertThat(result.a.b.d.val).isEqualTo(D_VAL);
            } else {
                assertThat(result.a.val).isNotEqualTo(A_VAL);
                assertThat(result.a.b.val).isNotEqualTo(B_VAL);
                assertThat(result.a.b.c.val).isNotEqualTo(C_VAL);
                assertThat(result.a.b.d.val).isNotEqualTo(D_VAL);
            }
        }
    }

    @Nested
    class WhenHThenEfg {
        private final Model<Root> model = Instancio.of(Root.class)
                .generate(field(H::val), gen -> gen.oneOf(H_VAL, OTHER_VAL))
                .assign(Assign.given(H::val).is(H_VAL)
                        .set(field(E::val), E_VAL)
                        .set(field(F::val), F_VAL)
                        .set(field(G::val), G_VAL))
                .toModel();

        @Test
        void create() {
            final Root result = Instancio.create(model);

            assertResult(result);
        }

        @Test
        void createList() {
            final List<Root> results = Instancio.ofList(model).create();

            assertThat(results)
                    .isNotEmpty()
                    .allSatisfy(this::assertResult);
        }

        private void assertResult(final Root result) {
            if (result.e.f.g.h.val.equals(H_VAL)) {
                assertThat(result.e.val).isEqualTo(E_VAL);
                assertThat(result.e.f.val).isEqualTo(F_VAL);
                assertThat(result.e.f.g.val).isEqualTo(G_VAL);
            } else {
                assertThat(result.e.val).isNotEqualTo(E_VAL);
                assertThat(result.e.f.val).isNotEqualTo(F_VAL);
                assertThat(result.e.f.g.val).isNotEqualTo(G_VAL);
            }
        }
    }

    @Nested
    class WhenFThenBh {
        private final Model<Root> model = Instancio.of(Root.class)
                .generate(field(F::val), gen -> gen.oneOf(F_VAL, OTHER_VAL))
                .assign(Assign.given(F::val).is(F_VAL)
                        .set(field(B::val), B_VAL)
                        .set(field(H::val), H_VAL))
                .toModel();

        @Test
        void create() {
            final Root result = Instancio.create(model);

            assertResult(result);
        }

        @Test
        void createList() {
            final List<Root> results = Instancio.ofList(model).create();

            assertThat(results)
                    .isNotEmpty()
                    .allSatisfy(this::assertResult);
        }

        private void assertResult(final Root result) {
            if (result.e.f.val.equals(F_VAL)) {
                assertThat(result.a.b.val).isEqualTo(B_VAL);
                assertThat(result.e.f.g.h.val).isEqualTo(H_VAL);
            } else {
                assertThat(result.a.b.val).isNotEqualTo(B_VAL);
                assertThat(result.e.f.g.h.val).isNotEqualTo(H_VAL);
            }
        }
    }

    @Nested
    class WhenHThenFWhenFThenDWhenDThenA {
        private final Model<Root> model = Instancio.of(Root.class)
                .generate(field(H::val), gen -> gen.oneOf(H_VAL, OTHER_VAL))
                .assign(Assign.given(H::val).is(H_VAL).set(field(F::val), F_VAL))
                .assign(Assign.given(F::val).is(F_VAL).set(field(D::val), D_VAL))
                .assign(Assign.given(D::val).is(D_VAL).set(field(A::val), A_VAL))
                .toModel();

        @Test
        void create() {
            final Root result = Instancio.create(model);

            assertResult(result);
        }

        @Test
        void createList() {
            final List<Root> results = Instancio.ofList(model).create();

            assertThat(results)
                    .isNotEmpty()
                    .allSatisfy(this::assertResult);
        }

        private void assertResult(final Root result) {
            if (result.e.f.g.h.val.equals(H_VAL)) {
                assertThat(result.a.val).isEqualTo(A_VAL);
                assertThat(result.a.b.d.val).isEqualTo(D_VAL);
                assertThat(result.e.f.val).isEqualTo(F_VAL);
            } else {
                assertThat(result.a.val).isNotEqualTo(A_VAL);
                assertThat(result.a.b.d.val).isNotEqualTo(D_VAL);
                assertThat(result.e.f.val).isNotEqualTo(F_VAL);
            }
        }
    }

    private record Root(String val, A a, E e) {}

    private record A(String val, B b) {}

    private record B(String val, C c, D d) {}

    private record C(String val) {}

    private record D(String val) {}

    private record E(String val, F f) {}

    private record F(String val, G g) {}

    private record G(String val, H h) {}

    private record H(String val) {}
}
