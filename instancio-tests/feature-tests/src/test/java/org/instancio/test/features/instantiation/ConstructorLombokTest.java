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

import lombok.Data;
import lombok.NonNull;
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

@FeatureTag(Feature.INSTANTIATION_STRATEGIES)
@ExtendWith(InstancioExtension.class)
class ConstructorLombokTest {

    @Value
    private static class ValuePojo {
        @NonNull
        String name;
        int age;
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

    /**
     * The generated null check rejects the null, proving the constructor
     * was invoked rather than the fields being assigned reflectively.
     */
    @Test
    void valuePojoIsInstantiatedViaConstructor() {
        final ValuePojo result = Instancio.create(ValuePojo.class);
        assertThat(result.getName()).isNotBlank();
        assertThat(result.getAge()).isNotZero();

        final InstancioApi<ValuePojo> api = Instancio.of(ValuePojo.class)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .set(field(ValuePojo::getName), null);

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("failed instantiating an object via constructor")
                .hasRootCauseInstanceOf(NullPointerException.class);
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
}
