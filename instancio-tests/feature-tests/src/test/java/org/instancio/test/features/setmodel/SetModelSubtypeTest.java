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
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.lists.ListString;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.MODEL, Feature.SET_MODEL, Feature.SUBTYPE})
@ExtendWith(InstancioExtension.class)
class SetModelSubtypeTest {

    private static class StringsGhiExt extends StringsGhi {}

    @Test
    void subtype() {
        final Model<StringsDef> model = Instancio.of(StringsDef.class)
                .subtype(all(StringsGhi.class), StringsGhiExt.class)
                .toModel();

        final StringsAbc result = Instancio.of(StringsAbc.class)
                .setModel(all(StringsDef.class), model)
                .create();

        assertThat(result.getDef().getGhi()).isExactlyInstanceOf(StringsGhiExt.class);
    }

    @Test
    void generatorSpecSubtype() {
        final Model<List<String>> model = Instancio.of(new TypeToken<List<String>>() {})
                .generate(allStrings(), gen -> gen.string().digits())
                .generate(all(List.class), gen -> gen.collection().subtype(LinkedList.class))
                .toModel();

        final ListString result = Instancio.of(ListString.class)
                .setModel(all(List.class), model)
                .create();

        assertThat(result.getList())
                .isExactlyInstanceOf(LinkedList.class)
                .isNotEmpty()
                .allSatisfy(s -> assertThat(s).containsOnlyDigits());
    }
}
