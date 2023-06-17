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
package org.instancio.test.features.assign;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.given;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.field;

@FeatureTag({Feature.ASSIGN, Feature.MODEL})
@ExtendWith(InstancioExtension.class)
class AssignWithModelTest {

    @Test
    void createFromModel() {
        final Model<StringsAbc> model = Instancio.of(StringsAbc.class)
                .assign(valueOf(StringsAbc::getA).to(StringsAbc::getB))
                .toModel();

        final StringsAbc result = Instancio.create(model);

        assertThat(result.getB()).isNotNull().isEqualTo(result.getA());
    }

    @Test
    void createCollectionFromModel() {
        final Model<StringsAbc> model = Instancio.of(StringsAbc.class)
                .assign(given(StringsAbc::getA).satisfies(s -> true)
                        .set(field(StringsAbc::getB), "foo")
                        .set(field(StringsAbc::getC), "bar"))
                .toModel();

        final List<StringsAbc> results = Instancio.ofList(model).create();

        assertThat(results).isNotEmpty().allSatisfy(result -> {
            assertThat(result.getB()).isEqualTo("foo");
            assertThat(result.getC()).isEqualTo("bar");
        });
    }

    @Test
    void modelOverride() {
        final Model<StringsAbc> model1 = Instancio.of(StringsAbc.class)
                .assign(given(StringsAbc::getA).satisfies(s -> true).set(field(StringsAbc::getB), "foo"))
                .toModel();

        final Model<StringsAbc> model2 = Instancio.of(model1)
                .assign(given(StringsAbc::getA).satisfies(s -> true).set(field(StringsAbc::getB), "bar"))
                .toModel();

        final StringsAbc result = Instancio.create(model2);

        assertThat(result.getB()).isEqualTo("bar");
    }
}
