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
package org.instancio.test.features.values;

import org.instancio.Gen;
import org.instancio.generator.specs.CharacterSpec;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Gen.chars;

@FeatureTag(Feature.VALUE_SPEC)
class CharacterSpecTest extends AbstractValueSpecTestTemplate<Character> {

    @Override
    protected CharacterSpec spec() {
        return Gen.chars();
    }

    @Test
    void range() {
        final int size = 1000;
        final List<Character> result = chars().range('x', 'z').list(size);
        assertThat(result).containsOnly('x', 'y', 'z');
    }
}
