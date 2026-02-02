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
package org.instancio.test.features.fill;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.generics.basic.NonGenericItemStringExtension;
import org.instancio.test.support.pojo.generics.inheritance.WithGenericParent;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.FILL)
@ExtendWith(InstancioExtension.class)
class FillGenericsTest {

    @Test
    void shouldPopulateClassWithGenericFields() {
        final WithGenericParent object = new WithGenericParent();

        Instancio.fill(object);

        assertThat(object.getGenericChild().getChildValue()).isNotBlank();
        assertThat(object.getGenericParent().getParentValue()).isPositive();
        assertThat(object.getGenericGrandChild().getChildValue()).isNotNull();
        assertThat(object.getGenericGrandChild().getParentValue()).isNotNull();
    }

    @Test
    void shouldPopulateClassThatExtendsGenericType() {
        final NonGenericItemStringExtension object = new NonGenericItemStringExtension();

        Instancio.fill(object);

        assertThat(object.getValue()).isNotBlank();
    }
}
