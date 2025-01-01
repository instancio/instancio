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
package org.instancio.test.features.seed;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.junit.WithSettings;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.dynamic.DynAddress;
import org.instancio.test.support.pojo.dynamic.DynPerson;
import org.instancio.test.support.pojo.dynamic.DynPet;
import org.instancio.test.support.pojo.dynamic.DynPhone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith.MethodAssignmentOnly
@FeatureTag({Feature.WITH_SEED, Feature.ASSIGNMENT_TYPE_METHOD})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(InstancioExtension.class)
class DynamicPojoSeedTest {

    private static final long SEED = 123;

    @SuppressWarnings("FieldCanBeLocal")
    private static DynPerson first;

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE)
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE);

    @Seed(SEED)
    @Order(1)
    @Test
    void first() {
        first = Instancio.create(DynPerson.class);

        assertThat(first).isNotNull();
        assertThat(first.getName())
                .as("Dynamic attributes should be populated")
                .isNotBlank();
    }

    @Seed(SEED)
    @Order(2)
    @Test
    void second() {
        final DynPerson second = Instancio.create(DynPerson.class);

        assertThat(first.getUuid()).isEqualTo(second.getUuid());
        assertThat(first.getName()).isEqualTo(second.getName());
        assertThat(first.getGender()).isEqualTo(second.getGender());
        assertThat(first.getAge()).isEqualTo(second.getAge());
        assertThat(first.getLastModified()).isEqualTo(second.getLastModified());
        assertThat(first.getDate()).isEqualTo(second.getDate());

        final DynPet[] pets1 = first.getPets();
        final DynPet[] pets2 = second.getPets();

        assertThat(pets1).isNotEmpty().hasSameSizeAs(pets2);

        for (int i = 0; i < pets1.length; i++) {
            assertThat(pets1[i].getName()).isEqualTo(pets2[i].getName());
        }

        final DynAddress address1 = first.getAddress();
        final DynAddress address2 = second.getAddress();

        assertThat(address1.getAddress()).isEqualTo(address2.getAddress());
        assertThat(address1.getCity()).isEqualTo(address2.getCity());
        assertThat(address1.getCountry()).isEqualTo(address2.getCountry());

        final List<DynPhone> phones1 = address1.getPhoneNumbers();
        final List<DynPhone> phones2 = address2.getPhoneNumbers();

        assertThat(phones1).isNotEmpty().hasSameSizeAs(phones2);

        for (int i = 0; i < phones1.size(); i++) {
            final DynPhone phone1 = phones1.get(i);
            final DynPhone phone2 = phones2.get(i);

            assertThat(phone1.getCountryCode()).isEqualTo(phone2.getCountryCode());
            assertThat(phone1.getNumber()).isEqualTo(phone2.getNumber());
        }
    }
}
