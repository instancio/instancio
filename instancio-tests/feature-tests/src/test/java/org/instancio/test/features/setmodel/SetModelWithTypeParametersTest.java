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
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.basic.ItemInterfaceHolder;
import org.instancio.test.support.pojo.generics.basic.ItemInterfaceStringHolder;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.MODEL, Feature.SET_MODEL, Feature.WITH_TYPE_PARAMETERS})
@ExtendWith(InstancioExtension.class)
class SetModelWithTypeParametersTest {

    private static final String EXPECTED = "-foo-";

    /**
     * Model using {@code withTypeParameters()}
     */
    @SuppressWarnings("rawtypes")
    private final Model<ItemInterface> model = Instancio.of(ItemInterface.class)
            .withTypeParameters(String.class)
            .subtype(all(ItemInterface.class), Item.class)
            .set(field(Item<String>::getValue), EXPECTED)
            .toModel();

    @Test
    void withNonGenericClass() {
        final ItemInterfaceStringHolder result = Instancio.of(ItemInterfaceStringHolder.class)
                .setModel(all(ItemInterface.class), model)
                .create();

        assertThat(result.getItemInterfaceString().getValue()).isEqualTo(EXPECTED);
    }

    @Test
    void withGenericClass() {
        final ItemInterfaceHolder<String> result = Instancio.of(new TypeToken<ItemInterfaceHolder<String>>() {})
                .setModel(all(ItemInterface.class), model)
                .create();

        assertThat(result.getItemInterface().getValue()).isEqualTo(EXPECTED);
    }

    @Test
    void derivedModel() {
        final Model<ItemInterfaceHolder<String>> derivedModel = Instancio.of(new TypeToken<ItemInterfaceHolder<String>>() {})
                .setModel(all(ItemInterface.class), model)
                .toModel();

        final ItemInterfaceHolder<String> result = Instancio.create(derivedModel);

        assertThat(result.getItemInterface().getValue()).isEqualTo(EXPECTED);
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void modelAsParameterizedTypeArgument() {
        final Model<StringHolder> stringHolderModel = Instancio.of(StringHolder.class)
                .set(field("value"), EXPECTED)
                .toModel();

        final Model<ItemInterface> itemInterfaceModel = Instancio.of(ItemInterface.class)
                .withTypeParameters(StringHolder.class)
                .subtype(all(ItemInterface.class), Item.class)
                .toModel();

        final ItemInterface<StringHolder> result = Instancio.of(itemInterfaceModel)
                .setModel(all(StringHolder.class), stringHolderModel)
                .create();

        assertThat(result.getValue().getValue()).isEqualTo(EXPECTED);
    }
}