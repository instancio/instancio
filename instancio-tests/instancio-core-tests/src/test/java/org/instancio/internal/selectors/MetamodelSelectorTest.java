/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.internal.selectors;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.instancio.Selector;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetamodelSelectorTest {

    @Test
    void create() {
        final MetamodelSelector selector = (MetamodelSelector) MetamodelSelector.of(StringHolder.class, "value");
        assertThat(selector.getTargetClass()).isEqualTo(StringHolder.class);
        assertThat(selector.getFieldName()).isEqualTo("value");
        assertThat(selector.isFieldSelector()).isTrue();
        assertThat(selector.getScopes()).isEmpty();
        assertThat(selector.getParent()).isNull();
    }

    @Test
    void copyWithNewStackTraceHolder() {
        final MetamodelSelector selector = (MetamodelSelector) MetamodelSelector.of(StringHolder.class, "value");
        final Selector copy = selector.copyWithNewStackTraceHolder();

        assertThat(copy)
                .isEqualTo(selector)
                .isExactlyInstanceOf(SelectorImpl.class);

        assertThat(((SelectorImpl) copy).getStackTraceHolder()).isNotSameAs(selector.getStackTraceHolder());
    }

    @Test
    void verifyEqualsAndHashcode() {
        EqualsVerifier.forClass(MetamodelSelector.class)
                .withIgnoredFields("parent", "stackTraceHolder")
                .verify();
    }
}
