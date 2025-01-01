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
package org.instancio.test.features.assign.adhoc;

import lombok.Data;
import org.instancio.Assign;
import org.instancio.Assignment;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignYoYoTest {

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void yoYo() {
        //@formatter:off
        @Data class L4 { String m, n, o; }
        @Data class L3 { String j, k, l; L4 l4; }
        @Data class L2 { String g, h, i; L3 l3; }
        @Data class L1 { String d, e, f; L2 l2; }
        @Data class L0 { String a, b, c; L1 l1; }
        //@formatter:on

        // c->o, o->b, b->n, n->a, a->m, m->d, d->j,
        // j->e, e->k, k->f, f->l, l->i, i->g, g->h
        final Assignment[] assignments = Instancio.gen().shuffle(
                Assign.given(L0::getC).is("C").set(field(L4::getO), "O"),
                Assign.given(L4::getO).is("O").set(field(L0::getB), "B"),
                Assign.given(L0::getB).is("B").set(field(L4::getN), "N"),
                Assign.given(L4::getN).is("N").set(field(L0::getA), "A"),
                Assign.given(L0::getA).is("A").set(field(L4::getM), "M"),
                Assign.given(L4::getM).is("M").set(field(L1::getD), "D"),
                Assign.given(L1::getD).is("D").set(field(L3::getJ), "J"),
                Assign.given(L3::getJ).is("J").set(field(L1::getE), "E"),
                Assign.given(L1::getE).is("E").set(field(L3::getK), "K"),
                Assign.given(L3::getK).is("K").set(field(L1::getF), "F"),
                Assign.given(L1::getF).is("F").set(field(L3::getL), "L"),
                Assign.given(L3::getL).is("L").set(field(L2::getI), "I"),
                Assign.given(L2::getI).is("I").set(field(L2::getG), "G"),
                Assign.given(L2::getG).is("G").set(field(L2::getH), "H")
        ).get().toArray(new Assignment[0]);

        final L0 result = Instancio.of(L0.class)
                .set(field(L0::getC), "C")
                .assign(assignments)
                .create();

        assertThat(result.a).isEqualTo("A");
        assertThat(result.b).isEqualTo("B");
        assertThat(result.c).isEqualTo("C");
        assertThat(result.l1.d).isEqualTo("D");
        assertThat(result.l1.e).isEqualTo("E");
        assertThat(result.l1.f).isEqualTo("F");
        assertThat(result.l1.l2.g).isEqualTo("G");
        assertThat(result.l1.l2.h).isEqualTo("H");
        assertThat(result.l1.l2.i).isEqualTo("I");
        assertThat(result.l1.l2.l3.j).isEqualTo("J");
        assertThat(result.l1.l2.l3.k).isEqualTo("K");
        assertThat(result.l1.l2.l3.l).isEqualTo("L");
        assertThat(result.l1.l2.l3.l4.m).isEqualTo("M");
        assertThat(result.l1.l2.l3.l4.n).isEqualTo("N");
        assertThat(result.l1.l2.l3.l4.o).isEqualTo("O");
    }
}
