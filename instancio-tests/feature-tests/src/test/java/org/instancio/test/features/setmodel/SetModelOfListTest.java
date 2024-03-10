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
package org.instancio.test.features.setmodel;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.MODEL, Feature.SET_MODEL, Feature.OF_LIST})
@ExtendWith(InstancioExtension.class)
class SetModelOfListTest {

    private final Model<StringsDef> modelDef = Instancio.of(StringsDef.class)
            .set(field(StringsDef::getD), "d")
            .toModel();

    private final Model<StringsGhi> modelGhi = Instancio.of(StringsGhi.class)
            .set(field(StringsGhi::getH), "h")
            .toModel();

    @Test
    void ofList() {
        final List<StringsAbc> results = Instancio.ofList(StringsAbc.class)
                .setModel(all(StringsDef.class), modelDef)
                .setModel(all(StringsGhi.class), modelGhi)
                .create();

        assertThat(results).isNotEmpty()
                .allMatch(res -> res.getDef().getD().equals("d"))
                .allMatch(res -> res.getDef().getGhi().getH().equals("h"));
    }

    @Test
    void elementModelWithSetModel() {

        // element model with setModel()
        final Model<StringsAbc> elementModel = Instancio.of(StringsAbc.class)
                .setModel(all(StringsDef.class), modelDef)
                .toModel();

        final List<StringsAbc> results = Instancio.ofList(elementModel)
                .setModel(all(StringsGhi.class), modelGhi)
                .create();

        assertThat(results).isNotEmpty()
                .allMatch(res -> res.getDef().getD().equals("d"))
                .allMatch(res -> res.getDef().getGhi().getH().equals("h"));
    }
}
