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
package org.instancio.test.features.model;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.SupportedNumericTypes;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.field;
import static org.instancio.Select.types;

@NonDeterministicTag
@FeatureTag({Feature.MODEL, Feature.WITH_NULLABLE})
@ExtendWith(InstancioExtension.class)
class ModelWithNullableTest {

    private static final int SAMPLE_SIZE = 500;

    @Test
    void verifyNullable() {
        final Model<SupportedNumericTypes> model = Instancio.of(SupportedNumericTypes.class)
                .withNullable(field("byteWrapper"))
                .withNullable(all(Float.class))
                .withNullable(allInts())
                .withNullable(types().of(Long.class))
                .toModel();

        assertThat(collectResults(model, SupportedNumericTypes::getByteWrapper)).containsNull();
        assertThat(collectResults(model, SupportedNumericTypes::getFloatWrapper)).containsNull();
        assertThat(collectResults(model, SupportedNumericTypes::getIntegerWrapper)).containsNull();
        assertThat(collectResults(model, SupportedNumericTypes::getLongWrapper)).containsNull();
    }

    private static Set<Object> collectResults(final Model<SupportedNumericTypes> model,
                                              final Function<SupportedNumericTypes, Object> collectFn) {

        final Set<Object> results = new HashSet<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final SupportedNumericTypes result = Instancio.create(model);
            results.add(collectFn.apply(result));
        }
        return results;
    }

}