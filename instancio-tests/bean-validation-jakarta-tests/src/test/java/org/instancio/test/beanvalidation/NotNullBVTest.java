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
package org.instancio.test.beanvalidation;

import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.pojo.beanvalidation.NotNullBv;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;
import static org.instancio.Select.all;
import static org.instancio.Select.allBytes;
import static org.instancio.Select.allDoubles;
import static org.instancio.Select.allFloats;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allLongs;
import static org.instancio.Select.allShorts;
import static org.instancio.Select.allStrings;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class NotNullBVTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.STRING_NULLABLE, true)
            .set(Keys.BYTE_NULLABLE, true)
            .set(Keys.SHORT_NULLABLE, true)
            .set(Keys.INTEGER_NULLABLE, true)
            .set(Keys.LONG_NULLABLE, true)
            .set(Keys.FLOAT_NULLABLE, true)
            .set(Keys.DOUBLE_NULLABLE, true)
            .set(Keys.ARRAY_NULLABLE, true)
            .set(Keys.COLLECTION_NULLABLE, true)
            .set(Keys.MAP_NULLABLE, true);

    @Test
    @NonDeterministicTag
    void notNullWithNullablesViaSettings() {
        final List<NotNullBv> result = Instancio.of(NotNullBv.class)
                .stream()
                .limit(SAMPLE_SIZE_DD)
                .collect(Collectors.toList());

        assertThat(result)
                .hasSize(SAMPLE_SIZE_DD)
                .allSatisfy(res -> assertThatObject(res).hasNoNullFieldsOrProperties());
    }

    @Test
    @NonDeterministicTag
    void withNullableHasHigherPrecedenceThanBeanValidationAnnotations() {
        final TargetSelector nullables = Select.all(
                allStrings(),
                allBytes(),
                allShorts(),
                allInts(),
                allLongs(),
                allFloats(),
                allDoubles(),
                all(BigInteger.class),
                all(BigDecimal.class),
                all(String[].class),
                all(Collection.class),
                all(Map.class));

        final List<NotNullBv> result = Instancio.of(NotNullBv.class)
                .withNullable(nullables)
                .stream()
                .limit(SAMPLE_SIZE_DD)
                .collect(Collectors.toList());

        assertThat(result)
                .hasSize(SAMPLE_SIZE_DD)
                .anyMatch(o -> o.getByteWrapper() == null)
                .anyMatch(o -> o.getShortWrapper() == null)
                .anyMatch(o -> o.getIntegerWrapper() == null)
                .anyMatch(o -> o.getLongWrapper() == null)
                .anyMatch(o -> o.getFloatWrapper() == null)
                .anyMatch(o -> o.getDoubleWrapper() == null)
                .anyMatch(o -> o.getBigInteger() == null)
                .anyMatch(o -> o.getBigDecimal() == null)
                .anyMatch(o -> o.getArray() == null)
                .anyMatch(o -> o.getCollection() == null)
                .anyMatch(o -> o.getMap() == null);
    }
}
