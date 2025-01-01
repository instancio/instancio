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
package org.instancio.test.features.setmodel;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.scope;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.MODEL, Feature.SET_MODEL})
@ExtendWith(InstancioExtension.class)
class SetModelAdhocTest {

    @Test
    void modelContainingGroupSelector() {
        final TargetSelector selectorGroup = all(
                allStrings().within(scope(StringsDef::getD)),
                allStrings().within(scope(StringsDef::getE)));

        final Model<StringsDef> model = Instancio.of(StringsDef.class)
                .ignore(allStrings().within(scope(StringsGhi.class)))
                .set(selectorGroup, "foo")
                .toModel();

        final StringsAbc result = Instancio.of(StringsAbc.class)
                .setModel(all(StringsDef.class), model)
                .create();

        assertThat(result.getDef().getD()).isEqualTo("foo");
        assertThat(result.getDef().getE()).isEqualTo("foo");
        assertThatObject(result.getDef().getGhi()).hasAllFieldsOfTypeSetToNull(String.class);
    }

    @Test
    void modelContainingSelectorsWithDifferentScopes() {
        final Model<StringsDef> model = Instancio.of(StringsDef.class)
                .ignore(allStrings().within(scope(StringsGhi.class)))
                .set(allStrings().within(scope(StringsDef::getD)), "d")
                .set(allStrings().within(scope(StringsDef::getE)), "e")
                .toModel();

        final StringsAbc result = Instancio.of(StringsAbc.class)
                .setModel(all(StringsDef.class), model)
                .create();

        assertThat(result.getDef().getD()).isEqualTo("d");
        assertThat(result.getDef().getE()).isEqualTo("e");
        assertThatObject(result.getDef().getGhi()).hasAllFieldsOfTypeSetToNull(String.class);
    }
}
