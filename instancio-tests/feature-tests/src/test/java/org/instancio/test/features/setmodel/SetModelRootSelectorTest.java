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

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;
import static org.instancio.Select.types;

/**
 * Related to https://github.com/instancio/instancio/issues/1159
 */
@FeatureTag({Feature.MODEL, Feature.ROOT_SELECTOR, Feature.SET_MODEL})
@ExtendWith(InstancioExtension.class)
class SetModelRootSelectorTest {

    private static final String CALLBACK_A_VALUE = "From callback A";
    private static final String CALLBACK_B_VALUE = "From callback B";

    //@formatter:off
    private static @Data class EntityA { private String property; }
    private static @Data class EntityB { private String property; }
    //@formatter:on

    private static final Model<EntityA> ENTITY_A_MODEL = Instancio.of(EntityA.class)
            .onComplete(root(), (EntityA entity) -> entity.setProperty(CALLBACK_A_VALUE))
            .toModel();

    private static final Model<EntityB> ENTITY_B_MODEL = Instancio.of(EntityB.class)
            .onComplete(root(), (EntityB entity) -> entity.setProperty(CALLBACK_B_VALUE))
            .toModel();


    @Test
    void givenTwoModelsWithOnCompleteRootSelectors_bothCallbacksShouldBeCalled() {
        final EntityA a = createEntity(EntityA.class);
        final EntityB b = createEntity(EntityB.class);

        assertThat(a.property).isEqualTo(CALLBACK_A_VALUE);
        assertThat(b.property).isEqualTo(CALLBACK_B_VALUE);
    }

    private static <T> T createEntity(final Class<T> type) {
        return Instancio.of(type)
                .lenient()
                .setModel(types().of(EntityA.class), ENTITY_A_MODEL)
                .setModel(types().of(EntityB.class), ENTITY_B_MODEL)
                .create();
    }
}