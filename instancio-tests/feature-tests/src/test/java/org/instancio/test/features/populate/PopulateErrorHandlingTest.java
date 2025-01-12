/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.test.features.populate;

import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag(Feature.POPULATE)
@ExtendWith(InstancioExtension.class)
class PopulateErrorHandlingTest {

    @Test
    void ofObjectWithNull() {
        assertThatThrownBy(() -> Instancio.ofObject(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("object to populate must not be null");
    }

    @Test
    void populateWithNull() {
        assertThatThrownBy(() -> Instancio.populate(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("object to populate must not be null");
    }

    @Test
    void emptyCollection() {
        final List<String> object = new ArrayList<>();

        assertThatThrownBy(() -> Instancio.ofObject(object))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("cannot populate an empty collection");

    }

    @Test
    void emptyMap() {
        final Map<String, String> object = new HashMap<>();

        assertThatThrownBy(() -> Instancio.ofObject(object))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("cannot populate an empty map");

    }
}
