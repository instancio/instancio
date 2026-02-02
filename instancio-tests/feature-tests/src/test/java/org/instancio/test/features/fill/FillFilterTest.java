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
package org.instancio.test.features.fill;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.FILTER, Feature.FILL})
@ExtendWith(InstancioExtension.class)
class FillFilterTest {

    private static final int GENERATED_INTEGER_VALUE = -1;

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.INTEGER_MIN, GENERATED_INTEGER_VALUE)
            .set(Keys.INTEGER_MAX, GENERATED_INTEGER_VALUE);

    @Test
    @DisplayName("filter() predicate is not be evaluated against initialised fields")
    void filterIsNotEvaluatedAgainstInitialisedFields() {
        final int initialisedValue = 12345;

        final IntegerHolder initialised = new IntegerHolder();
        initialised.setWrapper(initialisedValue);

        final List<IntegerHolder> list = Arrays.asList(initialised, new IntegerHolder());

        Instancio.ofObject(list)
                .filter(field(IntegerHolder::getWrapper), val -> val.equals(GENERATED_INTEGER_VALUE))
                .fill();

        assertThat(list).first().satisfies(holder -> {
            assertThat(holder.getPrimitive()).isEqualTo(GENERATED_INTEGER_VALUE);
            assertThat(holder.getWrapper()).isEqualTo(initialisedValue);
        });

        assertThat(list).last().satisfies(holder -> {
            assertThat(holder.getPrimitive()).isEqualTo(GENERATED_INTEGER_VALUE);
            assertThat(holder.getWrapper()).isEqualTo(GENERATED_INTEGER_VALUE);
        });
    }
}
