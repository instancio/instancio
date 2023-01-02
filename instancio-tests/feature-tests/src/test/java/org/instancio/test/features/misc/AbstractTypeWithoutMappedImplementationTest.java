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
package org.instancio.test.features.misc;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.interfaces.MultipleInterfaceImpls;
import org.instancio.test.support.pojo.interfaces.SingleInterfaceImpl;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Out of the box, Instancio does not attempt to resolve implementations of abstract types.
 */
@FeatureTag(Feature.UNSUPPORTED)
class AbstractTypeWithoutMappedImplementationTest {

    @Test
    void singleInterfaceImpl() {
        SingleInterfaceImpl.WidgetContainer widgetContainer = Instancio.create(SingleInterfaceImpl.WidgetContainer.class);

        assertThat(widgetContainer).isNotNull();
        assertThat(widgetContainer.getWidget()).isNull();
    }

    @Test
    void multipleInterfaceImpls() {
        MultipleInterfaceImpls.WidgetContainer widgetContainer = Instancio.create(MultipleInterfaceImpls.WidgetContainer.class);

        assertThat(widgetContainer).isNotNull();
        assertThat(widgetContainer.getWidget()).isNull();
    }
}
