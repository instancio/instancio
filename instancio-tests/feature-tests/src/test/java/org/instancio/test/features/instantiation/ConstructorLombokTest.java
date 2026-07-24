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
package org.instancio.test.features.instantiation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.OnConstructorError;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

/**
 * Lombok generates constructors whose parameters take the names of the fields
 * they are assigned to, so parameter names resolve as they do for hand-written
 * constructors. These tests pin that down, since a regression would be silent:
 * if names could not be resolved, {@code AUTO} mode would fall back to
 * instantiating without a constructor and the fields would still be populated.
 *
 * <p>To distinguish the two paths, the classes below use {@link NonNull}, which
 * makes Lombok emit a null check in the generated constructor. Passing a null
 * value therefore fails only if the constructor was actually invoked.
 *
 * <p>Like the other tests in this package, these rely on being compiled with
 * debug information, which the Maven build enables by default.
 */
@FeatureTag(Feature.INSTANTIATION_STRATEGIES)
@ExtendWith(InstancioExtension.class)
class ConstructorLombokTest {

    @Value
    private static class ValuePojo {
        @NonNull
        String name;
        int age;
    }

    /**
     * Covers the local variable slot arithmetic via Lombok: {@code long} and
     * {@code double} occupy two slots each, whereas {@code long[]} is
     * a reference and occupies one.
     */
    @Value
    private static class WideValuePojo {
        long id;
        double amount;
        long[] values;
        @NonNull
        String name;
        int count;
    }

    @Data
    private static class DataPojo {
        private @Nullable String name;
        private int age;
    }

    @Data
    private static class DataWithFinalFields {
        private final @NonNull String name;
        private final int age;
    }

    @AllArgsConstructor
    private static class AllArgsPojo {
        private @NonNull String name;
        private int age;
    }

    @RequiredArgsConstructor
    private static class RequiredArgsPojo {
        private final @NonNull String name;
        private final @Nullable String ignored = null;
    }

    /**
     * {@code @Builder} generates an all-args constructor, not a
     * {@code Pojo(Builder)} constructor, so it is not excluded by the
     * builder-constructor heuristic and is used like any other. The
     * generated builder class is named {@code <ClassName>Builder}, which
     * does not match the "Builder" simple name the heuristic looks for.
     */
    @Builder
    private static class BuilderPojo {
        private @NonNull String name;
        private int age;
    }

    @Value
    @Builder
    private static class ValueBuilderPojo {
        @NonNull
        String name;
        int age;
    }

    /**
     * A single-field {@code @Builder} class: its all-args constructor has one
     * parameter, the shape the builder-constructor heuristic inspects. It must
     * still be used, since the parameter type is {@code String}, not "Builder".
     */
    @Builder
    private static class SingleFieldBuilderPojo {
        private @NonNull String name;
    }

    @Test
    void valuePojoIsPopulated() {
        final ValuePojo result = Instancio.create(ValuePojo.class);

        assertThat(result.getName()).isNotBlank();
        assertThat(result.getAge()).isNotZero();
    }

    @Test
    void selectorAppliesToValuePojoConstructorParameter() {
        final ValuePojo result = Instancio.of(ValuePojo.class)
                .set(field(ValuePojo::getName), "Bonnie")
                .create();

        assertThat(result.getName()).isEqualTo("Bonnie");
    }

    /**
     * The generated null check rejects the null, proving the constructor
     * was invoked rather than the fields being assigned reflectively.
     */
    @Test
    void valuePojoIsInstantiatedViaConstructor() {
        final InstancioApi<ValuePojo> api = Instancio.of(ValuePojo.class)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .set(field(ValuePojo::getName), null);

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("failed instantiating an object via constructor")
                .hasRootCauseInstanceOf(NullPointerException.class);
    }

    @Test
    void wideValuePojoIsPopulated() {
        final WideValuePojo result = Instancio.create(WideValuePojo.class);

        assertThat(result.getId()).isNotZero();
        assertThat(result.getAmount()).isNotZero();
        assertThat(result.getValues()).isNotEmpty();
        assertThat(result.getName()).isNotBlank();
        assertThat(result.getCount()).isNotZero();
    }

    /**
     * Each parameter must be matched to its own field: if the slot widths of
     * {@code long}/{@code double} were miscalculated, the names would shift
     * and the parameters would be matched to the wrong fields, or not at all.
     */
    @Test
    void wideValuePojoIsInstantiatedViaConstructor() {
        final InstancioApi<WideValuePojo> api = Instancio.of(WideValuePojo.class)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .set(field(WideValuePojo::getName), null);

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasRootCauseInstanceOf(NullPointerException.class);
    }

    /**
     * {@code @Data} implies {@code @RequiredArgsConstructor}. With no final
     * fields that is a no-argument constructor, which has no parameters to
     * match, so the class is populated via fields or setters as usual.
     */
    @Test
    void dataPojoIsPopulated() {
        final DataPojo result = Instancio.create(DataPojo.class);

        assertThat(result.getName()).isNotBlank();
        assertThat(result.getAge()).isNotZero();
    }

    @Test
    void dataPojoWithFinalFieldsIsInstantiatedViaConstructor() {
        final DataWithFinalFields result = Instancio.create(DataWithFinalFields.class);
        assertThat(result.getName()).isNotBlank();
        assertThat(result.getAge()).isNotZero();

        final InstancioApi<DataWithFinalFields> api = Instancio.of(DataWithFinalFields.class)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .set(field(DataWithFinalFields::getName), null);

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasRootCauseInstanceOf(NullPointerException.class);
    }

    @Test
    void allArgsPojoIsInstantiatedViaConstructor() {
        final InstancioApi<AllArgsPojo> api = Instancio.of(AllArgsPojo.class)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .set(field(AllArgsPojo.class, "name"), null);

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasRootCauseInstanceOf(NullPointerException.class);
    }

    @Test
    void requiredArgsPojoIsInstantiatedViaConstructor() {
        final InstancioApi<RequiredArgsPojo> api = Instancio.of(RequiredArgsPojo.class)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .set(field(RequiredArgsPojo.class, "name"), null);

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasRootCauseInstanceOf(NullPointerException.class);
    }

    @Test
    void builderPojoIsPopulated() {
        final BuilderPojo result = Instancio.create(BuilderPojo.class);

        assertThat(result.name).isNotBlank();
        assertThat(result.age).isNotZero();
    }

    @Test
    void builderPojoIsInstantiatedViaConstructor() {
        final InstancioApi<BuilderPojo> api = Instancio.of(BuilderPojo.class)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .set(field(BuilderPojo.class, "name"), null);

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasRootCauseInstanceOf(NullPointerException.class);
    }

    @Test
    void valueBuilderPojoIsInstantiatedViaConstructor() {
        final ValueBuilderPojo result = Instancio.create(ValueBuilderPojo.class);
        assertThat(result.getName()).isNotBlank();
        assertThat(result.getAge()).isNotZero();

        final InstancioApi<ValueBuilderPojo> api = Instancio.of(ValueBuilderPojo.class)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .set(field(ValueBuilderPojo::getName), null);

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasRootCauseInstanceOf(NullPointerException.class);
    }

    @Test
    void singleFieldBuilderPojoIsInstantiatedViaConstructor() {
        final InstancioApi<SingleFieldBuilderPojo> api = Instancio.of(SingleFieldBuilderPojo.class)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .set(field(SingleFieldBuilderPojo.class, "name"), null);

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasRootCauseInstanceOf(NullPointerException.class);
    }
}
