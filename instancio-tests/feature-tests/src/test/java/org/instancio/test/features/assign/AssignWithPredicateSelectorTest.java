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
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.ASSIGN,
        Feature.PREDICATE_SELECTOR
})
@ExtendWith(InstancioExtension.class)
class AssignWithPredicateSelectorTest {

    private static final String INITIAL = "initial";
    private static final String FOO = "foo";

    @Test
    void fieldPredicateBuilder() {
        final StringFields result = Instancio.of(StringFields.class)
                .set(field(StringFields::getTwo), "2")
                .assign(Assign.given(fields().annotated(StringFields.Two.class))
                        .is("2")
                        .set(fields().named("one"), "1"))
                .create();

        assertThat(result.getOne()).isEqualTo("1");
        assertThat(result.getTwo()).isEqualTo("2");
    }

    @Test
    void whenA_setAllStringsUsingPredicateSelector() {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .set(field(StringsAbc::getA), INITIAL)
                .assign(Assign.given(fields().named("a"))
                        .is(INITIAL)
                        .set(types().of(String.class).atDepth(d -> d > 1), FOO))
                .create();

        assertThat(result.a).isEqualTo(INITIAL);
        assertThat(result.b).isNotBlank().isNotIn(INITIAL, FOO);
        assertThat(result.c).isNotBlank().isNotIn(INITIAL, FOO);
        assertThat(result.def.d).isEqualTo(FOO);
        assertThat(result.def.e).isEqualTo(FOO);
        assertThat(result.def.f).isEqualTo(FOO);
        assertThat(result.def.ghi.g).isEqualTo(FOO);
        assertThat(result.def.ghi.h).isEqualTo(FOO);
        assertThat(result.def.ghi.i).isEqualTo(FOO);
    }
}
