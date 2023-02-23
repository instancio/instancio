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
package org.instancio.test.features.values.id;

import org.instancio.Gen;
import org.instancio.generator.specs.EanSpec;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
class EanSpecTest extends AbstractValueSpecTestTemplate<String> {

    @Override
    protected EanSpec spec() {
        return Gen.id().ean();
    }

    @Override
    protected void assertDefaultSpecValue(final String actual) {
        assertThat(actual).containsOnlyDigits().hasSize(13);
    }

    @Test
    void type8() {
        assertThat(spec().type8().get())
                .containsOnlyDigits()
                .hasSize(8);
    }

    @Test
    void type13() {
        assertThat(spec().type13().get())
                .containsOnlyDigits()
                .hasSize(13);
    }
}
