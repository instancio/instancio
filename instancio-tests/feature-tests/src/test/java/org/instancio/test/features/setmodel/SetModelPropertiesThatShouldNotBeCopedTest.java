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
package org.instancio.test.features.setmodel;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Result;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

/**
 * When using {@code setModel()}, certain properties
 * of the model should not be copied (e.g. settings, seed, etc).
 */
@FeatureTag({
        Feature.MODEL,
        Feature.SET_MODEL,
        Feature.MAX_DEPTH,
        Feature.SETTINGS,
        Feature.WITH_SEED
})
@ExtendWith(InstancioExtension.class)
class SetModelPropertiesThatShouldNotBeCopedTest {

    @Test
    void settingsShouldNotBeCopiedFromModel() {
        final int modelMinSizeSetting = 100;

        final Model<Phone> model = Instancio.of(Phone.class)
                .withSetting(Keys.COLLECTION_MIN_SIZE, modelMinSizeSetting)
                .toModel();

        final Person result = Instancio.of(Person.class)
                .setModel(all(Phone.class), model)
                .create();

        assertThat(result.getAddress().getPhoneNumbers()).hasSizeLessThanOrEqualTo(modelMinSizeSetting);
    }

    @Test
    void maxDepthShouldNotBeCopiedFromModel() {
        final Model<Phone> model = Instancio.of(Phone.class)
                .withMaxDepth(0)
                .withSetting(Keys.MAX_DEPTH, 0)
                .toModel();

        final Person result = Instancio.of(Person.class)
                .setModel(all(Phone.class), model)
                .create();

        assertThatObject(result).isFullyPopulated();
    }

    @Test
    void seedShouldNotBeCopiedFromModel() {
        // use negative seeds to because by default random seeds are positive
        final long seed1 = -123;
        final long seed2 = -456;

        final Model<Phone> model = Instancio.of(Phone.class)
                .withSeed(seed1)
                .withSetting(Keys.SEED, seed2)
                .toModel();

        final Result<Person> result = Instancio.of(Person.class)
                .setModel(all(Phone.class), model)
                .asResult();

        assertThat(result.getSeed()).isNotIn(seed1, seed2);
    }
}
