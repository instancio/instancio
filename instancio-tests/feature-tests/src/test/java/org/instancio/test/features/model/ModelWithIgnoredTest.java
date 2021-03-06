/*
 * Copyright 2022 the original author or authors.
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
import org.instancio.test.support.pojo.basic.SupportedMathTypes;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.MODEL, Feature.IGNORE})
class ModelWithIgnoredTest {

    @Test
    void verifyIgnored() {
        final Model<SupportedMathTypes> model = Instancio.of(SupportedMathTypes.class)
                .ignore(field("bigInteger"))
                .ignore(all(BigDecimal.class))
                .toModel();

        final SupportedMathTypes result = Instancio.create(model);

        assertThat(result.getBigInteger()).isNull();
        assertThat(result.getBigDecimal()).isNull();
    }

}