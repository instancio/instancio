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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.interfaces.ListOfStringHolderInterface;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.elementOf;

/**
 * The {@code subtype()} implementation works at node-level
 * and does not support element selectors.
 */
@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.SUBTYPE})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorSubtypeTest {

    private static final List<TargetSelector> SELECTORS = List.of(
            elementOf(ListOfStringHolderInterface::getList),
            elementOf(ListOfStringHolderInterface::getList).first(),
            all(elementOf(ListOfStringHolderInterface::getList)) // group
    );

    @FeatureTag(Feature.UNSUPPORTED)
    @FieldSource("SELECTORS")
    @ParameterizedTest
    void subtype(final TargetSelector selector) {
        final InstancioApi<ListOfStringHolderInterface> api = Instancio.of(ListOfStringHolderInterface.class);

        assertThatThrownBy(() -> api.subtype(selector, StringHolder.class))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("subtype() does not support elementOf() selectors");
    }
}
