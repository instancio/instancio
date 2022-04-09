/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.api.features.model;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.test.support.pojo.basic.SupportedNumericTypes;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.allInts;
import static org.instancio.Bindings.field;

@NonDeterministicTag
class ModelWithSupplyTest {

    @Test
    void supply() {
        final long longValue = 111;
        final int intValue = 222;

        final Model<SupportedNumericTypes> model = Instancio.of(SupportedNumericTypes.class)
                .supply(field("primitiveLong"), () -> longValue)
                .supply(allInts(), () -> intValue)
                .toModel();

        final SupportedNumericTypes result = Instancio.create(model);
        assertThat(result.getPrimitiveInt()).isEqualTo(intValue);
        assertThat(result.getIntegerWrapper()).isEqualTo(intValue);
        assertThat(result.getPrimitiveLong()).isEqualTo(longValue);
        assertThat(result.getLongWrapper()).isNotNull().isNotEqualTo(longValue);
    }
}
