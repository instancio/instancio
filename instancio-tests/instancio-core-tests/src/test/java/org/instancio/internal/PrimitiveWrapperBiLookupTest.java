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
package org.instancio.internal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PrimitiveWrapperBiLookupTest {

    @Test
    void isAssignableConsideringBoxing() {
        assertThat(PrimitiveWrapperBiLookup.isAssignableConsideringBoxing(int.class, int.class)).isTrue();
        assertThat(PrimitiveWrapperBiLookup.isAssignableConsideringBoxing(int.class, Integer.class)).isTrue();
        assertThat(PrimitiveWrapperBiLookup.isAssignableConsideringBoxing(Integer.class, int.class)).isTrue();
        assertThat(PrimitiveWrapperBiLookup.isAssignableConsideringBoxing(Number.class, int.class)).isTrue();
        assertThat(PrimitiveWrapperBiLookup.isAssignableConsideringBoxing(Object.class, int.class)).isTrue();
        assertThat(PrimitiveWrapperBiLookup.isAssignableConsideringBoxing(CharSequence.class, String.class)).isTrue();

        assertThat(PrimitiveWrapperBiLookup.isAssignableConsideringBoxing(Number.class, boolean.class)).isFalse();
        assertThat(PrimitiveWrapperBiLookup.isAssignableConsideringBoxing(long.class, int.class)).isFalse();
        assertThat(PrimitiveWrapperBiLookup.isAssignableConsideringBoxing(String.class, int.class)).isFalse();
        assertThat(PrimitiveWrapperBiLookup.isAssignableConsideringBoxing(String.class, CharSequence.class)).isFalse();
    }
}
