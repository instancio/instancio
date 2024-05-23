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
package org.instancio.test.features.blank;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.types;

@FeatureTag({Feature.BLANK, Feature.ON_COMPLETE})
@ExtendWith(InstancioExtension.class)
class BlankOnCompleteTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FAIL_ON_ERROR, true);

    @Test
    void onComplete() {
        final List<Class<?>> callbackTypes = new ArrayList<>();

        Instancio.ofBlank(Person.class)
                .onComplete(types(), o -> callbackTypes.add(o.getClass()))
                .create();

        // Callbacks only called on these types because everything else is null
        assertThat(callbackTypes).containsExactlyInAnyOrder(
                Address.class, Person.class, Pet[].class, ArrayList.class);
    }
}
