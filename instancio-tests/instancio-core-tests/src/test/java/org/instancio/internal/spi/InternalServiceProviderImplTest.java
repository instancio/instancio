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
package org.instancio.internal.spi;

import org.instancio.internal.spi.InternalServiceProviderImpl.InternalGetterMethodFieldResolverImpl;
import org.instancio.test.support.pojo.misc.getters.BeanStylePojo;
import org.instancio.test.support.pojo.misc.getters.PropertyStylePojo;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InternalServiceProviderImplTest {

    @Nested
    class InternalGetterMethodFieldResolverImplTest {
        private final InternalGetterMethodFieldResolverImpl resolver = new InternalGetterMethodFieldResolverImpl();

        @Test
        void resolveFieldName() {
            // java beans style
            assertThat(resolver.resolveField(BeanStylePojo.class, "getFoo").getName()).isEqualTo("foo");
            assertThat(resolver.resolveField(BeanStylePojo.class, "isBar").getName()).isEqualTo("bar");

            // property style getters (e.g. java records)
            assertThat(resolver.resolveField(PropertyStylePojo.class, "foo").getName()).isEqualTo("foo");
            assertThat(resolver.resolveField(PropertyStylePojo.class, "bar").getName()).isEqualTo("bar");
            assertThat(resolver.resolveField(PropertyStylePojo.class, "isBaz").getName()).isEqualTo("isBaz");
            assertThat(resolver.resolveField(PropertyStylePojo.class, "haz").getName()).isEqualTo("haz");
            assertThat(resolver.resolveField(PropertyStylePojo.class, "hasGaz").getName()).isEqualTo("hasGaz");
        }
    }
}
