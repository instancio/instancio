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
package org.instancio.test.beanvalidation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.pojo.beanvalidation.FooFieldBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for using custom generator provider implemented
 * in {@link org.spi.SampleSpi} with Bean Validation.
 */
@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class FooFieldBVTest {

    @Test
    void withNotNull() {
        final FooFieldBV.WithNotNull result = Instancio.create(FooFieldBV.WithNotNull.class);
        assertThat(result.getFoo()).isEqualTo("foo");
    }

    /**
     * Test using a primary annotation, such as {@code @Email}.
     */
    @Test
    void withNotNullAndEmail() {
        final FooFieldBV.WithNotNullAndEmail result = Instancio.create(FooFieldBV.WithNotNullAndEmail.class);
        assertThat(result.getFoo()).isEqualTo("foo");
    }

}
