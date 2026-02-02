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
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

/**
 * Model's selectors can be overridden when creating an object.
 * The selectors specified when creating the object have higher
 * precedence than model selectors.
 */
@FeatureTag({Feature.MODEL, Feature.SET_MODEL, Feature.SET, Feature.GENERATE})
@ExtendWith(InstancioExtension.class)
class SetModelPrecedenceTest {

    private static final String VALUE_FROM_MODEL = "from-model";
    private static final String OVERRIDE_VALUE = "override-value";

    @Test
    @DisplayName("setModel() then set()")
    void setModel_thenSet_shouldUseOverriddenValueFromSet() {
        final Model<StringsDef> model = Instancio.of(StringsDef.class)
                .set(field(StringsDef::getD).lenient(), VALUE_FROM_MODEL)
                .toModel();

        final StringsAbc result = Instancio.of(StringsAbc.class)
                // set() should take precedence over setModel()
                // method order should not matter!
                .setModel(all(StringsDef.class), model)
                .set(field(StringsDef::getD), OVERRIDE_VALUE)
                .create();

        assertThat(result.getDef().getD()).isEqualTo(OVERRIDE_VALUE);
    }

    @Test
    @DisplayName("set() then setModel()")
    void set_thenSetModel_shouldUseOverriddenValueFromSet() {
        final Model<StringsDef> model = Instancio.of(StringsDef.class)
                .set(field(StringsDef::getD).lenient(), VALUE_FROM_MODEL)
                .toModel();

        final StringsAbc result = Instancio.of(StringsAbc.class)
                // set() should take precedence over setModel()
                // method order should not matter!
                .set(field(StringsDef::getD), OVERRIDE_VALUE)
                .setModel(all(StringsDef.class), model)
                .create();

        assertThat(result.getDef().getD()).isEqualTo(OVERRIDE_VALUE);
    }

    @Test
    void overrideSelectorUsingTheSameScope_shouldNotThrowUnusedSelectorError() {
        final Model<StringsDef> model = Instancio.of(StringsDef.class)
                // lenient() not required because we override using the same scope
                .set(field(StringsDef::getD), VALUE_FROM_MODEL)
                .toModel();

        final StringsAbc result = Instancio.of(StringsAbc.class)
                .setModel(all(StringsDef.class), model)
                .set(field(StringsDef::getD).within(scope(StringsDef.class)), OVERRIDE_VALUE)
                .create();

        assertThat(result.getDef().getD()).isEqualTo(OVERRIDE_VALUE);
    }
}
