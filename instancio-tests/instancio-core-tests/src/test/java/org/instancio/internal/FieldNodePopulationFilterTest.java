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
package org.instancio.internal;

import org.instancio.exception.InstancioException;
import org.instancio.generator.AfterGenerate;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.SystemProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FieldNodePopulationFilterTest {

    private final FieldNodePopulationFilter filter = new FieldNodePopulationFilter();

    @Test
    @SetSystemProperty(key = SystemProperties.FAIL_ON_ERROR, value = "true")
    @DisplayName("Should throw error on null field if fail-on-error is enabled")
    void nullFieldWithFailOnErrorEnabled() {
        final InternalNode nodeWithNullField = InternalNode.ignoredNode();
        final Object anyObject = new Object();

        assertThatThrownBy(() -> filter.shouldSkip(
                nodeWithNullField, AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES, anyObject))
                .isExactlyInstanceOf(InstancioException.class)
                .hasMessage("Node has a null field: %s", nodeWithNullField);
    }
}
