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
package org.instancio.test.beanvalidation.adhoc;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.types;
import static org.instancio.settings.Keys.BEAN_VALIDATION_ENABLED;
import static org.instancio.settings.Keys.COLLECTION_MAX_SIZE;
import static org.instancio.settings.Keys.COLLECTION_MIN_SIZE;

/**
 * See: https://github.com/instancio/instancio/issues/1278
 */
@ExtendWith(InstancioExtension.class)
class GH1278Test {

    @Data
    private static final class SampleClass {
        @NotEmpty
        private Collection<String> notEmptyCollection;

        @Size(min = 1)
        private Collection<String> collectionWithMinSize;

        private Collection<String> collection;
    }

    /**
     * This Maven module uses instancio.properties with custom settings.
     * Revert to defaults for this test class.
     */
    @WithSettings
    private static final Settings settings = Settings.defaults();

    @Test
    void collectionSizeValidationEnabled() {
        final SampleClass result = Instancio.of(SampleClass.class)
                .withSetting(COLLECTION_MIN_SIZE, 1)
                .withSetting(COLLECTION_MAX_SIZE, 1)
                .withSetting(BEAN_VALIDATION_ENABLED, true)
                .create();

        assertCollectionsOfSizeOne(result);
    }

    @Test
    void collectionSizeValidationDisabled() {
        final SampleClass result = Instancio.of(SampleClass.class)
                .withSetting(COLLECTION_MIN_SIZE, 1)
                .withSetting(COLLECTION_MAX_SIZE, 1)
                .withSetting(BEAN_VALIDATION_ENABLED, false)
                .create();

        assertCollectionsOfSizeOne(result);
    }

    @Test
    void collectionSizeInGeneratorValidationEnabled() {
        final SampleClass result = Instancio.of(SampleClass.class)
                .withSetting(BEAN_VALIDATION_ENABLED, true)
                .generate(types().of(Collection.class), gen -> gen.collection().size(1))
                .create();

        assertCollectionsOfSizeOne(result);
    }

    @Test
    void collectionSizeInGeneratorValidationDisabledTest() {
        final SampleClass result = Instancio.of(SampleClass.class)
                .withSetting(BEAN_VALIDATION_ENABLED, false)
                .generate(types().of(Collection.class), gen -> gen.collection().size(1))
                .create();

        assertCollectionsOfSizeOne(result);
    }

    private static void assertCollectionsOfSizeOne(final SampleClass result) {
        assertThat(result.getNotEmptyCollection()).hasSize(1);
        assertThat(result.getCollectionWithMinSize()).hasSize(1);
        assertThat(result.getCollection()).hasSize(1);
    }
}
