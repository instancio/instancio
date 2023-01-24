/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.test.features.values;

import org.instancio.Gen;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.MODEL, Feature.VALUE_SPEC})
@ExtendWith(InstancioExtension.class)
class ValueSpecToModelTest {

    @Test
    void createFromModel() {
        final Model<String> model = Gen.string().digits().toModel();
        final String result = Instancio.create(model);
        assertThat(result).containsOnlyDigits();
    }

    @Test
    void createCollectionFromModel() {
        final int collectionSize = 5;
        final int stringLength = 50;
        final Model<String> model = Gen.string().length(stringLength).toModel();
        final Set<String> result = Instancio.ofSet(model).size(collectionSize).create();

        assertThat(result)
                .hasSize(collectionSize)
                .allSatisfy(s -> assertThat(s).hasSize(stringLength));
    }
}
