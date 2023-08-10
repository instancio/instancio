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
package org.instancio.test.features.values.id.pol;

import org.instancio.Gen;
import org.instancio.generator.specs.RegonSpec;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
class RegonSpecTest extends AbstractValueSpecTestTemplate<String> {

    @Override
    protected RegonSpec spec() {
        return Gen.id().pol().regon();
    }

    @Override
    protected void assertDefaultSpecValue(final String actual) {
        assertThat(actual)
                .containsOnlyDigits()
                .hasSize(9);
    }

    @Test
    void type9() {
        assertThat(spec().type9().get())
                .containsOnlyDigits()
                .hasSize(9);
    }

    @Test
    void type14() {
        assertThat(spec().type14().get())
                .containsOnlyDigits()
                .hasSize(14);
    }
}
