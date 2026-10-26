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
package org.instancio.spi.tests;

import org.example.spi.CustomTemplateProvider;
import org.example.spi.CustomTemplateProvider.TemplateId;
import org.example.spi.CustomTemplateProvider.TemplatePojo;
import org.example.spi.CustomTemplateProvider.TemplateRecord;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

/**
 * Tests for {@link CustomTemplateProvider#getTemplateProvider()}.
 */
@ExtendWith(InstancioExtension.class)
class TemplateProviderSpiTest {

    private static final int SAMPLE_SIZE = 500;

    private static List<TemplateRecord> sample(final InstancioApi<TemplateRecord> api) {
        return api.stream().limit(SAMPLE_SIZE).toList();
    }

    @Test
    void shouldApplyTemplateWithoutAnyApiCalls() {
        final List<TemplateRecord> results = sample(Instancio.of(TemplateRecord.class));

        assertThat(results).allSatisfy(r -> assertThat(r.name()).startsWith("template_"));
        assertThat(results).extracting(TemplateRecord::nickname).containsNull();
        assertThat(results).extracting(TemplateRecord::id).containsNull();
        assertThat(results).anySatisfy(r -> assertThat(r.nickname()).isNotNull());
        assertThat(results).anySatisfy(r -> assertThat(r.id()).isNotNull());
    }

    @Test
    void shouldApplyTemplateIgnore() {
        final TemplatePojo result = Instancio.create(TemplatePojo.class);

        assertThat(result.initialised).isEqualTo("initial");
        assertThat(result.other).isNotBlank();
    }

    @Test
    void templateNullabilityShouldApplyToTypeLevelSupply() {
        final List<TemplateRecord> results = sample(Instancio.of(TemplateRecord.class)
                .supply(all(TemplateId.class), () -> new TemplateId("supplied")));

        assertThat(results).extracting(TemplateRecord::id)
                .containsNull()
                .contains(new TemplateId("supplied"));
    }

    @Test
    void localSelectorShouldOverrideTemplateSelector() {
        final List<TemplateRecord> results = sample(Instancio.of(TemplateRecord.class)
                .set(field(TemplateRecord::name), "local"));

        assertThat(results).extracting(TemplateRecord::name).containsOnly("local");
    }

    @Test
    void templateSelectorsShouldBeLenient() {
        assertThat(Instancio.create(String.class)).isNotBlank();
    }

    @Test
    void shouldApplyTemplateOnceWhenCreatingFromModel() {
        final Model<TemplateRecord> model = Instancio.of(TemplateRecord.class)
                .set(field(TemplateRecord::name), "model")
                .toModel();

        final List<TemplateRecord> results = sample(Instancio.of(model));

        assertThat(results).extracting(TemplateRecord::name).containsOnly("model");
    }

    /**
     * Known limitation: template {@code withNullable()} still applies
     * to fields populated via a local {@code set()}.
     */
    @Test
    void templateNullabilityShouldApplyToLocalSet() {
        final List<TemplateRecord> results = sample(Instancio.of(TemplateRecord.class)
                .set(field(TemplateRecord::nickname), "local"));

        assertThat(results).extracting(TemplateRecord::nickname).containsNull().contains("local");
    }
}
