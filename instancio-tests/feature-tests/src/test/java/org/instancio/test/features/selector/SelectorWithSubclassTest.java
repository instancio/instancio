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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.pojo.inheritance.BaseClassSubClassInheritance.BaseClass;
import org.instancio.test.support.pojo.inheritance.BaseClassSubClassInheritance.SubClass;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

@FeatureTag({Feature.SELECTOR, Feature.SUPPLY})
class SelectorWithSubclassTest {

    @Test
    @DisplayName("If field is defined in a parent class, need to specify the class explicitly")
    void setParentClassFieldWithoutSpecifyingSelectorClass() {
        final InstancioApi<SubClass> api = Instancio.of(SubClass.class)
                .set(field("privateBaseClassField"), "foo");

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Invalid field 'privateBaseClassField'");
    }

    @Test
    @DisplayName("Selecting value by specifying parent class and field")
    void setParentClassFieldSpecifyingSelectorClass() {
        final SubClass result = Instancio.of(SubClass.class)
                .set(field(BaseClass.class, "privateBaseClassField"), "foo")
                .create();

        assertThat(result.getPrivateBaseClassField()).isEqualTo("foo");
    }
}
