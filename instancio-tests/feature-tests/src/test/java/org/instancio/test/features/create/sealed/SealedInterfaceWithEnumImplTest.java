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
package org.instancio.test.features.create.sealed;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

/**
 * See: https://github.com/instancio/instancio/issues/1777
 */
@ExtendWith(InstancioExtension.class)
class SealedInterfaceWithEnumImplTest {

    private sealed interface SealedWithEnumAndRecord permits EnumImpl, RecordImpl {}

    private enum EnumImpl implements SealedWithEnumAndRecord {ONE, TWO, THREE}

    private record RecordImpl(String value) implements SealedWithEnumAndRecord {}

    private sealed interface SealedWithEnumOnly permits SingleEnumImpl {}

    private enum SingleEnumImpl implements SealedWithEnumOnly {A, B, C}

    @Test
    void sealedInterfaceWithEnumAndRecordImplementations() {
        final List<SealedWithEnumAndRecord> results = Instancio.of(SealedWithEnumAndRecord.class)
                .withSetting(Keys.FAIL_ON_ERROR, true)
                .stream()
                .limit(Constants.SAMPLE_SIZE_DD)
                .toList();

        assertThat(results)
                .hasSize(Constants.SAMPLE_SIZE_DD)
                .doesNotContainNull()
                .filteredOn(EnumImpl.class::isInstance)
                .isNotEmpty();

        assertThat(results)
                .filteredOn(RecordImpl.class::isInstance)
                .isNotEmpty()
                .allSatisfy(r -> assertThat(((RecordImpl) r).value()).is(Conditions.RANDOM_STRING));
    }

    @Test
    void sealedInterfaceWithEnumOnlyImplementation() {
        final List<SealedWithEnumOnly> results = Instancio.of(SealedWithEnumOnly.class)
                .withSettings(Settings.create().set(Keys.FAIL_ON_ERROR, true))
                .stream()
                .limit(Constants.SAMPLE_SIZE_DD)
                .toList();

        assertThat(results)
                .hasSize(Constants.SAMPLE_SIZE_DD)
                .doesNotContainNull()
                .containsAnyOf(SingleEnumImpl.A, SingleEnumImpl.B, SingleEnumImpl.C);
    }

    @Test
    @FeatureTag(Feature.SUBTYPE)
    void subtypeMappingToEnumImplementation() {
        final SealedWithEnumAndRecord result = Instancio.of(SealedWithEnumAndRecord.class)
                .withSettings(Settings.create().set(Keys.FAIL_ON_ERROR, true))
                .subtype(all(SealedWithEnumAndRecord.class), EnumImpl.class)
                .create();

        assertThat(result).isInstanceOf(EnumImpl.class);
    }

    @Test
    @FeatureTag(Feature.SUBTYPE)
    void subtypeMappingToEnumImplementationViaSettings() {
        final SealedWithEnumAndRecord result = Instancio.of(SealedWithEnumAndRecord.class)
                .withSettings(Settings.create()
                        .set(Keys.FAIL_ON_ERROR, true)
                        .mapType(SealedWithEnumAndRecord.class, EnumImpl.class))
                .create();

        assertThat(result).isInstanceOf(EnumImpl.class);
    }
}
