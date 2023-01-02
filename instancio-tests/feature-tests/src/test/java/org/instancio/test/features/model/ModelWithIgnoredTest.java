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
package org.instancio.test.features.model;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.test.support.pojo.basic.SupportedTemporalTypes;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;

@FeatureTag({Feature.MODEL, Feature.IGNORE})
class ModelWithIgnoredTest {

    @Test
    void verifyIgnored() {
        final Model<SupportedTemporalTypes> model = Instancio.of(SupportedTemporalTypes.class)
                .ignore(field("instant"))
                .ignore(all(LocalDate.class))
                .ignore(fields().ofType(LocalDateTime.class))
                .toModel();

        final SupportedTemporalTypes result = Instancio.create(model);

        assertThat(result.getInstant()).isNull();
        assertThat(result.getLocalDate()).isNull();
        assertThat(result.getLocalDateTime()).isNull();
    }

}