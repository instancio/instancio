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
package org.instancio.test.features.conditional.adhoc;

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.valueOf;

/**
 * Adhoc tests based on the following structure:
 *
 * <pre>
 *           Root
 *        / / | \ \
 *       A B  C  D E
 * </pre>
 */
@FeatureTag(Feature.CONDITIONAL)
@ExtendWith(InstancioExtension.class)
class ConditionalFlatPojosTest {

    private static final String
            A_VAL = "A",
            B_VAL = "B",
            C_VAL = "C",
            D_VAL = "D",
            E_VAL = "E";

    @Test
    void whenC_thenA_whenA_thenE_whenE_thenB() {
        final Root result = Instancio.of(Root.class)
                .set(field(C::getVal), C_VAL)
                .when(valueOf(C::getVal).is(C_VAL).set(field(A::getVal), A_VAL))
                .when(valueOf(A::getVal).is(A_VAL).set(field(E::getVal), E_VAL))
                .when(valueOf(E::getVal).is(E_VAL).set(field(B::getVal), B_VAL))
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
                .set(field(E::getVal), E_VAL)
                .when(valueOf(E::getVal).is(E_VAL).set(field(D::getVal), D_VAL))
                .when(valueOf(D::getVal).is(D_VAL).set(field(C::getVal), C_VAL))
                .when(valueOf(C::getVal).is(C_VAL).set(field(B::getVal), B_VAL))
                .when(valueOf(B::getVal).is(B_VAL).set(field(A::getVal), A_VAL))
                .create();

        assertThat(result.a.val).isEqualTo(A_VAL);
        assertThat(result.b.val).isEqualTo(B_VAL);
        assertThat(result.c.val).isEqualTo(C_VAL);
        assertThat(result.d.val).isEqualTo(D_VAL);
        assertThat(result.e.val).isEqualTo(E_VAL);
    }


    private static @Data class Root {
        private A a;
        private B b;
        private C c;
        private D d;
        private E e;
    }

    // @formatter:off
    private static @Data class A { private String val; }
    private static @Data class B { private String val; }
    private static @Data class C { private String val; }
    private static @Data class D { private String val; }
    private static @Data class E { private String val; }
    // @formatter:on
}
