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
import nl.jqno.equalsverifier.Warning;
import org.instancio.Select;
import org.instancio.Selector;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.scope;
import static org.instancio.testsupport.asserts.SelectorAssert.assertSelector;

class PrimitiveAndWrapperSelectorImplTest {

    @Test
    void withinReturnsANewSelectorInstance() {
        final PrimitiveAndWrapperSelectorImpl selector = (PrimitiveAndWrapperSelectorImpl) Select.allInts();
        final PrimitiveAndWrapperSelectorImpl scopedSelector = (PrimitiveAndWrapperSelectorImpl) selector.within(scope(IntegerHolder.class));

        assertThat(selector)
                .as("within() should return a new selector")
                .isNotSameAs(scopedSelector)
                .isNotEqualTo(scopedSelector);

        assertSelector(selector).isCoreTypeSelectorWithoutScope(int.class, Integer.class);
        assertSelector(scopedSelector).isCoreTypeSelector(int.class, Integer.class);
        assertThat(scopedSelector.flatten().get(0).getScopes()).containsExactly(scope(IntegerHolder.class));
        assertThat(scopedSelector.flatten().get(1).getScopes()).containsExactly(scope(IntegerHolder.class));
    }

    @Test
    void verifyEqualsAndHashcode() {
        EqualsVerifier.forClass(PrimitiveAndWrapperSelectorImpl.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    void verifyToString() {
        assertThat(Select.allInts()).hasToString("PrimitiveAndWrapperSelector[Selector[(int)], Selector[(Integer)]]");
    }

    @Test
    void flatten() {
        final Selector selector = Select.allBooleans();
        final List<SelectorImpl> results = ((Flattener) selector).flatten();
        assertThat(results).hasSize(2);
        assertSelector(results.get(0)).isClassSelectorWithNoScope().hasTargetClass(boolean.class);
        assertSelector(results.get(1)).isClassSelectorWithNoScope().hasTargetClass(Boolean.class);
    }
}
