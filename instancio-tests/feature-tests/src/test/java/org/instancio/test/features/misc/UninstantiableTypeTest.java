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
package org.instancio.test.features.misc;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.misc.UninstantiableArrayElementType;
import org.instancio.test.support.pojo.misc.UninstantiableCollectionElementType;
import org.instancio.test.support.pojo.misc.UninstantiableFieldType;
import org.instancio.test.support.pojo.misc.UninstantiableMapKeyValueType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UninstantiableTypeTest {

    @Test
    @DisplayName("If a field value cannot be generated, set it to null")
    void field() {
        final UninstantiableFieldType result = Instancio.create(UninstantiableFieldType.class);
        assertThat(result.getKlass()).isNull();
    }

    @Test
    @DisplayName("If a collection element value cannot be generated, do not add it to the collection")
    void collectionElementType() {
        final UninstantiableCollectionElementType result = Instancio.create(UninstantiableCollectionElementType.class);
        assertThat(result.getCollection()).isEmpty();
    }

    @Test
    @DisplayName("If map key or value cannot be generated, do not add it to the map")
    void mapKeyValueType() {
        final UninstantiableMapKeyValueType result = Instancio.create(UninstantiableMapKeyValueType.class);
        assertThat(result.getMapClassString()).isEmpty();
        assertThat(result.getMapStringClass()).isEmpty();
    }

    @Test
    @DisplayName("If an array element value cannot be generated, set it to null")
    void arrayElementType() {
        final UninstantiableArrayElementType result = Instancio.create(UninstantiableArrayElementType.class);
        assertThat(result.getArray()).containsOnlyNulls();
    }

}
