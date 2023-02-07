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
package org.instancio.test.features.maxdepth;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.performance.onetomany.OneToMany;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;

@FeatureTag({Feature.CYCLIC, Feature.MAX_DEPTH})
@ExtendWith(InstancioExtension.class)
class MaxDepthOneToManyTest {

    private static OneToMany createWithDepth(final int depth) {
        return Instancio.of(OneToMany.class)
                .withSettings(Settings.create().set(Keys.MAX_DEPTH, depth))
                .create();
    }

    private static void assertAllChildrenAreNullOrEmpty(final OneToMany result) {
        assertThat(result.getChildrenA()).isNullOrEmpty();
        assertThat(result.getChildrenB()).isNullOrEmpty();
        assertThat(result.getChildrenC()).isNullOrEmpty();
        assertThat(result.getChildrenD()).containsOnlyNulls(); // array
        assertThat(result.getChildrenE()).isNullOrEmpty();
    }

    @Test
    void depth0() {
        final OneToMany result = createWithDepth(0);

        assertThatObject(result)
                .isNotNull()
                .hasAllNullFieldsOrProperties();
    }

    @Test
    void depth1() {
        final OneToMany result = createWithDepth(1);

        assertThatObject(result).hasNoNullFieldsOrProperties();
        assertAllChildrenAreNullOrEmpty(result);
    }

    @Test
    void depth2() {
        final OneToMany result = createWithDepth(2);

        assertThatObject(result).hasNoNullFieldsOrProperties();

        assertThat(result.getChildrenA())
                .isNotEmpty()
                .allSatisfy(o -> assertThatObject(o).isNotNull().hasAllNullFieldsOrProperties());

        assertThat(result.getChildrenB())
                .isNotEmpty()
                .allSatisfy(o -> assertThatObject(o).isNotNull().hasAllNullFieldsOrProperties());

        assertThat(result.getChildrenC())
                .isNotEmpty()
                .allSatisfy(o -> assertThatObject(o).isNotNull().hasAllNullFieldsOrProperties());

        assertThat(result.getChildrenE().values())
                .isNotEmpty()
                .allSatisfy(o -> assertThatObject(o).isNotNull().hasAllNullFieldsOrProperties());
    }

    @Test
    void depth3() {
        final OneToMany result = createWithDepth(3);

        assertThatObject(result).hasNoNullFieldsOrProperties();

        assertThat(result.getChildrenA())
                .isNotEmpty()
                .allSatisfy(o -> assertThatObject(o).isNotNull().hasNoNullFieldsOrProperties());

        assertThat(result.getChildrenB())
                .isNotEmpty()
                .allSatisfy(o -> assertThatObject(o).isNotNull().hasNoNullFieldsOrProperties());

        assertThat(result.getChildrenC())
                .isNotEmpty()
                .allSatisfy(o -> assertThatObject(o).isNotNull().hasNoNullFieldsOrProperties());

        assertThat(result.getChildrenE().values())
                .isNotEmpty()
                .allSatisfy(o -> assertThatObject(o).isNotNull().hasNoNullFieldsOrProperties());
    }

    @Test
    void depth4() {
        final OneToMany result = createWithDepth(4);

        // 0
        assertThat(result).isNotNull();

        // 1
        assertThatObject(result).hasNoNullFieldsOrProperties();

        // 2
        assertThat(result.getChildrenA())
                .isNotEmpty()
                .allSatisfy(o -> {
                    // 3
                    assertThatObject(o).isNotNull().hasNoNullFieldsOrProperties();
                    // 4
                    assertAllChildrenAreNullOrEmpty(o.getParent());
                });

        // 2
        assertThat(result.getChildrenB())
                .isNotEmpty()
                .allSatisfy(o -> {
                    // 3
                    assertThatObject(o).isNotNull().hasNoNullFieldsOrProperties();

                    // 4
                    assertAllChildrenAreNullOrEmpty(o.getParent());
                });

        // 2 ...
        assertThat(result.getChildrenC())
                .isNotEmpty()
                .allSatisfy(o -> {
                    assertThatObject(o).isNotNull().hasNoNullFieldsOrProperties();
                    assertAllChildrenAreNullOrEmpty(o.getParent());
                });

        assertThat(result.getChildrenE().values())
                .isNotEmpty()
                .allSatisfy(o -> {
                    assertThatObject(o).isNotNull().hasNoNullFieldsOrProperties();
                    assertAllChildrenAreNullOrEmpty(o.getParent());
                });
    }
}
