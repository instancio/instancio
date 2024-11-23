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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

/**
 * Related to https://github.com/instancio/instancio/issues/1215
 */
@FeatureTag({Feature.MODEL, Feature.ROOT_SELECTOR, Feature.SET_MODEL})
@ExtendWith(InstancioExtension.class)
class SetModelRootListSelectorTest {

    private static final int FOO_LIST_SIZE = 1;
    private static final int BAR_LIST_SIZE = 9;

    //@formatter:off
    private static @Data class Foo { private String fooValue; }
    private static @Data class Bar { private String barValue; }
    private static @Data class Container { private List<Foo> fooList; private List<Bar> barList; }
    //@formatter:on

    @Test
    void twoRootSelectorsWithSetModel() {
        final Model<List<Foo>> fooListModel = Instancio.ofList(Foo.class)
                .generate(root(), gen -> gen.collection().size(FOO_LIST_SIZE))
                .toModel();

        final Model<List<Bar>> barListModel = Instancio.ofList(Bar.class)
                .generate(root(), gen -> gen.collection().size(BAR_LIST_SIZE))
                .toModel();

        final Model<Container> containerModel = Instancio.of(Container.class)
                .setModel(field(Container::getFooList), fooListModel)
                .setModel(field(Container::getBarList), barListModel)
                .toModel();

        final Container container = Instancio.create(containerModel);

        assertThat(container.getFooList()).hasSize(FOO_LIST_SIZE);
        assertThat(container.getBarList()).hasSize(BAR_LIST_SIZE);
    }
}