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
import org.instancio.Selector;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.inheritance.BaseClassSubClassInheritance.BaseClass;
import org.instancio.test.support.pojo.inheritance.BaseClassSubClassInheritance.SubClass;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.instancio.Select.setter;

@FeatureTag({Feature.SELECTOR, Feature.SUPPLY})
@ExtendWith(InstancioExtension.class)
class SelectorWithSubclassTest {

    @Test
    @DisplayName("If field is defined in a parent class, need to specify the class explicitly")
    void setParentClassFieldWithoutSpecifyingSelectorTargetClass() {
        final String fieldName = "privateBaseClassField";
        final Selector selector = field(fieldName);
        final InstancioApi<SubClass> api = Instancio.of(SubClass.class);

        assertThatThrownBy(() -> api.set(selector, "foo"))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("invalid field '%s' for %s", fieldName, SubClass.class);
    }

    @Nested
    class SetBaseClassFieldSelectorTest {
        @Test
        @DisplayName("Selecting value by specifying parent class and field")
        void usingFieldSelector() {
            final SubClass result = Instancio.of(SubClass.class)
                    .set(field(BaseClass.class, "privateBaseClassField"), "foo")
                    .create();

            assertThat(result.getPrivateBaseClassField()).isEqualTo("foo");
        }

        @Test
        @RunWith.MethodAssignmentOnly
        @DisplayName("Selecting value by specifying parent class and method")
        void usingMethodFieldSelector() {
            final SubClass result = Instancio.of(SubClass.class)
                    .withSettings(Settings.create()
                            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL))
                    .set(setter(BaseClass.class, "setPrivateBaseClassField"), "foo")
                    .create();

            assertThat(result.getPrivateBaseClassField()).isEqualTo("foo");
        }
    }
}
