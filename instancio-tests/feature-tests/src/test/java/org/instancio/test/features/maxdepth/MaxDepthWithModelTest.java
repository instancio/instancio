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
package org.instancio.test.features.maxdepth;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.MAX_DEPTH, Feature.MODEL, Feature.SETTINGS})
@ExtendWith(InstancioExtension.class)
class MaxDepthWithModelTest {

    @Nested
    class CollectionFromModelTest {
        @Test
        void maxDepthViaBuilderApi() {
            final Model<Address> model = Instancio.of(Address.class)
                    .withMaxDepth(0)
                    .toModel();

            assertMaxDepth(model);
        }

        @Test
        void maxDepthViaSettings() {
            final Model<Address> model = Instancio.of(Address.class)
                    .withSettings(Settings.create().set(Keys.MAX_DEPTH, 0))
                    .toModel();

            assertMaxDepth(model);
        }

        private void assertMaxDepth(final Model<Address> model) {
            final List<Address> results = Instancio.ofList(model).create();

            assertThat(results).isNotEmpty().allSatisfy(result -> {
                assertThatObject(result).hasAllFieldsOfTypeSetToNull(String.class);
                assertThat(result.getPhoneNumbers()).isNull();
            });
        }
    }

    @Test
    void shouldInheritMaxDepthFromModelSpecifiedViaBuilderApi() {
        final Model<Address> model = Instancio.of(Address.class)
                .withMaxDepth(0)
                .toModel();

        final Address result = Instancio.create(model);

        assertThat(result).hasAllNullFieldsOrProperties();
    }

    @Test
    void shouldInheritMaxDepthFromModelSpecifiedViaSettings() {
        final Model<Address> model = Instancio.of(Address.class)
                .withSettings(Settings.create().set(Keys.MAX_DEPTH, 0))
                .toModel();

        final Address result = Instancio.create(model);

        assertThat(result).hasAllNullFieldsOrProperties();
    }

    @Test
    void overrideMaxDepthOfTheModel() {
        final Model<Address> model = Instancio.of(Address.class)
                .withSettings(Settings.create().set(Keys.MAX_DEPTH, 0))
                .toModel();

        final Address result = Instancio.of(model)
                .withMaxDepth(Integer.MAX_VALUE)
                .create();

        assertThat(result).usingRecursiveAssertion().hasNoNullFields();
    }
}
