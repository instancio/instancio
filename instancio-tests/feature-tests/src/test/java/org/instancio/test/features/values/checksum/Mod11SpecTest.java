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
package org.instancio.test.features.values.checksum;

import org.instancio.Gen;
import org.instancio.generator.specs.Mod11Spec;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
class Mod11SpecTest extends AbstractValueSpecTestTemplate<String> {

    // Value may contain 'X' since it is the default treatCheck10As character
    private static final String EXPECTED_PATTERN = "[0-9X]+";

    @Override
    protected Mod11Spec spec() {
        return Gen.checksum().mod11();
    }

    @Override
    protected void assertDefaultSpecValue(final String actual) {
        assertThat(actual)
                .matches(EXPECTED_PATTERN)
                .hasSize(16);
    }

    @Test
    void customised() {
        assertThat(spec().startIndex(2).endIndex(5).length(6).get())
                .matches(EXPECTED_PATTERN)
                .hasSize(6);
    }
}
