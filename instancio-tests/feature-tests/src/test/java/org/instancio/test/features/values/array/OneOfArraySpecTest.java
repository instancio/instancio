/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.features.values.array;

import org.instancio.Gen;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.ValueSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag(Feature.VALUE_SPEC)
@ExtendWith(InstancioExtension.class)
class OneOfArraySpecTest extends AbstractValueSpecTestTemplate<String> {

    private static final String[] CHOICES = {"foo", "bar", "baz"};

    @Override
    protected ValueSpec<String> spec() {
        return Gen.oneOf(CHOICES);
    }

    @Override
    protected void assertDefaultSpecValue(final String actual) {
        assertThat(actual).isIn(Arrays.asList(CHOICES));
    }

    @Test
    @Override
    protected void toModel() {
        final ValueSpec<String> spec = spec();
        assertThatThrownBy(spec::toModel)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("oneOf() spec does not support toModel()");
    }
}
