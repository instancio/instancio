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
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.specs.EnumSpec;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag(Feature.VALUE_SPEC)
class EnumSpecTest extends AbstractValueSpecTestTemplate<Gender> {

    @Override
    protected EnumSpec<Gender> spec() {
        return Gen.enumOf(Gender.class);
    }

    @Test
    void excluding() {
        final Gender result = spec().excluding(Gender.FEMALE, Gender.MALE).get();

        assertThat(result).isEqualTo(Gender.OTHER);
    }

    @Test
    @Override
    protected void toModel() {
        final EnumSpec<Gender> spec = spec();
        assertThatThrownBy(spec::toModel)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("enumOf() spec does not support toModel()");
    }
}
