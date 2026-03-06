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

import org.instancio.GetMethodSelector;
import org.instancio.Instancio;
import org.instancio.SetMethodSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.misc.NullableGetterSetterPojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.ASSIGNMENT_TYPE_METHOD, Feature.METHOD_REFERENCE_SELECTOR})
@ExtendWith(InstancioExtension.class)
class NullableGetterSetterPojoTest {

    @Test
    void getterSelector_withNullableReturnGetter() {
        // NOTE declared explicitly with @Nullable to ensure no compiler warnings/errors
        final GetMethodSelector<NullableGetterSetterPojo, @Nullable Integer> selector =
                NullableGetterSetterPojo::getValue;

        final NullableGetterSetterPojo result = Instancio.of(NullableGetterSetterPojo.class)
                .set(selector, null)
                .create();

        assertThat(result.getValue()).isNull();
    }

    @Test
    @RunWith.MethodAssignmentOnly
    void setterSelector_withNullableArgSetter() {
        // NOTE declared explicitly with @Nullable to ensure no compiler warnings/errors
        final SetMethodSelector<NullableGetterSetterPojo, @Nullable Integer> setter =
                NullableGetterSetterPojo::setValue;

        final NullableGetterSetterPojo result = Instancio.of(NullableGetterSetterPojo.class)
                .withSetting(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .set(setter, null)
                .create();

        assertThat(result.getValue()).isNull();
    }
}
