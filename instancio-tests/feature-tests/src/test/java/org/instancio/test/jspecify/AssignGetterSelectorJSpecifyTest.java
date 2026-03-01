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
package org.instancio.test.jspecify;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@ExtendWith(InstancioExtension.class)
class AssignGetterSelectorJSpecifyTest {

    @Test
    void assign_valueOf_to() {
        final PojoWithNullableGetters result = Instancio.of(PojoWithNullableGetters.class)
                .assign(Assign.valueOf(PojoWithNullableGetters::getFoo).to(PojoWithNullableGetters::getBar))
                .create();

        assertThat(result.getFoo()).isEqualTo(result.getBar());
    }

    @Test
    void assign_given_predicateSatisfies_set() {
        final PojoWithNullableGetters result = Instancio.of(PojoWithNullableGetters.class)
                .assign(Assign.given(PojoWithNullableGetters::getFoo)
                        .satisfies(any -> true)
                        .set(field(PojoWithNullableGetters::getBar), "bar"))
                .create();

        assertThat(result.getBar()).isEqualTo("bar");
    }
}
