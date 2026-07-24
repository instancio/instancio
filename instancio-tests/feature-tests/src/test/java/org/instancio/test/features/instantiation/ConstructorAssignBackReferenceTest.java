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
package org.instancio.test.features.instantiation;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.constructor.DetailCtorPojo;
import org.instancio.test.support.pojo.constructor.MainCtorPojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.root;

@FeatureTag({Feature.ASSIGN, Feature.INSTANTIATION_STRATEGIES, Feature.ROOT_SELECTOR})
@ExtendWith(InstancioExtension.class)
class ConstructorAssignBackReferenceTest {

    private static final List<TargetSelector> rootSelectors = List.of(root(), all(MainCtorPojo.class));

    @FieldSource("rootSelectors")
    @ParameterizedTest
    void backReferenceToRoot(final TargetSelector rootObjectSelector) {
        final MainCtorPojo result = Instancio.of(MainCtorPojo.class)
                .assign(Assign.valueOf(rootObjectSelector).to(DetailCtorPojo::getMainPojo))
                .assign(Assign.valueOf(MainCtorPojo::getId).to(DetailCtorPojo::getMainPojoId))
                .create();

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDetailPojos()).isNotEmpty().allSatisfy(detail -> {
            assertThat(detail.getId()).isNotNull();
            assertThat(detail.getMainPojo()).isSameAs(result);
            assertThat(detail.getMainPojoId()).isEqualTo(result.getId());
        });
    }

    @Test
    void createList() {
        final List<MainCtorPojo> results = Instancio.ofList(MainCtorPojo.class)
                .assign(Assign.valueOf(all(MainCtorPojo.class)).to(DetailCtorPojo::getMainPojo))
                .assign(Assign.valueOf(MainCtorPojo::getId).to(DetailCtorPojo::getMainPojoId))
                .create();

        assertThat(results).isNotEmpty().allSatisfy(result ->
                assertThat(result.getDetailPojos()).isNotEmpty().allSatisfy(detail -> {
                    assertThat(detail.getMainPojo()).isSameAs(result);
                    assertThat(detail.getMainPojoId()).isEqualTo(result.getId());
                }));
    }
}
