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
package org.instancio.test.features.values.hash;

import org.instancio.Gen;
import org.instancio.generator.specs.HashSpec;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
class HashSpecTest extends AbstractValueSpecTestTemplate<String> {

    @Override
    protected HashSpec spec() {
        return Gen.hash();
    }

    @Override
    protected void assertDefaultSpecValue(final String actual) {
        assertThat(actual).isHexadecimal().hasSize(32); // md5 by default
    }

    @Test
    void md5() {
        assertThat(spec().md5().get())
                .isHexadecimal()
                .hasSize(32);
    }

    @Test
    void sha1() {
        assertThat(spec().sha1().get())
                .isHexadecimal()
                .hasSize(40);
    }

    @Test
    void sha256() {
        assertThat(spec().sha256().get())
                .isHexadecimal()
                .hasSize(64);
    }

    @Test
    void sha512() {
        assertThat(spec().sha512().get())
                .isHexadecimal()
                .hasSize(128);
    }
}
