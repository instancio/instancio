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
package org.instancio.test.features.seed;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag({Feature.MODEL, Feature.WITH_SEED})
@ExtendWith(InstancioExtension.class)
class WithSeedNotAllowedWithToModelTest {

    @Test
    void withSeedThenToModel() {
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .withSeed(123);

        assertUsageError(api);
    }

    @Test
    void withSeedThenToModelOnDerivedModel() {
        final Model<Person> model = Instancio.of(Person.class)
                .toModel();

        final InstancioApi<Person> api = Instancio.of(model)
                .withSeed(123);

        assertUsageError(api);
    }

    private static void assertUsageError(InstancioApi<Person> api) {
        assertThatThrownBy(api::toModel)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("withSeed() cannot be used when creating a Model via toModel()");
    }
}
