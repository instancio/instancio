/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.pojo.basic.ClassWithInitializedField;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.field;

@NonDeterministicTag
class IgnoredFieldTest {

    @Test
    @DisplayName("Ignored field should retain the original value")
    void fieldIsIgnored() {
        final ClassWithInitializedField holder = Instancio.of(ClassWithInitializedField.class)
                .ignore(field("value"))
                .create();

        assertThat(holder.getValue()).isEqualTo(ClassWithInitializedField.DEFAULT_FIELD_VALUE);
    }
}
