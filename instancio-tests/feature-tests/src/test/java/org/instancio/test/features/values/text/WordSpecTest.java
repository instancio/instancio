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
package org.instancio.test.features.values.text;

import org.instancio.Instancio;
import org.instancio.generator.specs.WordSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
@ExtendWith(InstancioExtension.class)
class WordSpecTest extends AbstractValueSpecTestTemplate<String> {

    @Override
    protected WordSpec spec() {
        return Instancio.gen().text().word();
    }

    @Test
    void noun() {
        assertThat(spec().noun().get()).isNotNull();
    }

    @Test
    void verb() {
        assertThat(spec().verb().get()).isNotNull();
    }

    @Test
    void adjective() {
        assertThat(spec().adjective().get()).isNotNull();
    }

    @Test
    void adverb() {
        assertThat(spec().adverb().get()).isNotNull();
    }
}
