/*
 * Copyright 2022-2024 the original author or authors.
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
import org.instancio.Result;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

/**
 * Given: Model without a seed is assigned to a field.
 * <p>
 * Expectation: objects created from the model should
 * use seed value from the {@code @Seed} annotation.
 */
@FeatureTag({
        Feature.MODEL,
        Feature.WITH_SEED,
        Feature.WITH_SEED_ANNOTATION
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(InstancioExtension.class)
class ModelWithoutSeedAssignedToFieldTest {

    private static final int SEED = 1;

    private static Phone referencePhone;

    private final Model<Phone> model = Instancio.of(Phone.class)
            .set(field(Phone::getNumber), "12345")
            .toModel();

    @Test
    @Seed(SEED)
    @Order(1)
    void shouldUseSeedFromAnnotation() {
        final Result<Phone> result = Instancio.of(model).asResult();
        assertThat(result.getSeed()).isEqualTo(SEED);

        referencePhone = result.get();
        assertThat(referencePhone).isNotNull();
    }

    @Test
    @Seed(SEED)
    @Order(2)
    void shouldEqualReferencePhone() {
        final Phone phone2 = Instancio.create(model);
        assertThat(phone2).isNotNull().isEqualTo(referencePhone);
    }

    @Test
    @Seed(SEED)
    @Order(3)
    void modelOverrideSeed() {
        final long seedOverride = -123;
        final Model<Phone> derivedModel = Instancio.of(model)
                .withSeed(seedOverride)
                .toModel();

        final Result<Phone> result = Instancio.of(derivedModel).asResult();
        assertThat(result.getSeed()).isEqualTo(seedOverride);
        assertThat(result.get()).isNotNull().isNotEqualTo(referencePhone);
    }

    @Test
    @Seed(SEED)
    @Order(4)
    void objectOverrideSeed() {
        final long seedOverride = -123;
        final Result<Phone> result = Instancio.of(model)
                .withSeed(seedOverride)
                .asResult();

        assertThat(result.getSeed()).isEqualTo(seedOverride);
        assertThat(result.get()).isNotNull().isNotEqualTo(referencePhone);
    }
}
