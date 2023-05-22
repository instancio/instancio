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
package org.instancio.internal.reflect;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ParameterizedTypeImplTest {

    @Test
    void verifyEqualsAndHashCode() {
        EqualsVerifier.forClass(ParameterizedTypeImpl.class).verify();
    }

    @Test
    void ownerType() {
        final ParameterizedTypeImpl type = new ParameterizedTypeImpl(List.class, String.class);

        assertThat(type.getOwnerType()).isNull();
    }

    @Test
    void verifyToString() {
        final ParameterizedTypeImpl listOfStrings = new ParameterizedTypeImpl(List.class, String.class);

        assertThat(listOfStrings)
                .hasToString("List<String>");

        assertThat(new ParameterizedTypeImpl(List.class, listOfStrings))
                .hasToString("List<List<String>>");

        assertThat(new ParameterizedTypeImpl(Map.class, Integer.class, Long.class))
                .hasToString("Map<Integer, Long>");
    }
}
