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
package org.instancio.internal.selectors;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.instancio.Select;
import org.instancio.Selector;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.testsupport.asserts.SelectorAssert;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.scope;

class PrimitiveAndWrapperSelectorImplTest {

    @Test
    void withinReturnsANewSelectorInstance() {
        final PrimitiveAndWrapperSelectorImpl selector = (PrimitiveAndWrapperSelectorImpl) Select.allInts();
        final PrimitiveAndWrapperSelectorImpl scopedSelector = (PrimitiveAndWrapperSelectorImpl) selector.within(scope(IntegerHolder.class));

        assertThat(selector)
                .as("within() should return a new selector")
                .isNotSameAs(scopedSelector)
                .isNotEqualTo(scopedSelector);

        SelectorAssert.assertSelector(selector).isCoreTypeSelectorWithoutScope(int.class, Integer.class);
        SelectorAssert.assertSelector(scopedSelector).isCoreTypeSelector(int.class, Integer.class);
        assertThat(((SelectorImpl) scopedSelector.flatten().get(0)).getScopes()).containsExactly(scope(IntegerHolder.class));
        assertThat(((SelectorImpl) scopedSelector.flatten().get(1)).getScopes()).containsExactly(scope(IntegerHolder.class));
    }

    @Test
    void verifyEqualsAndHashcode() {
        EqualsVerifier.forClass(PrimitiveAndWrapperSelectorImpl.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

    @Test
    void verifyToString() {
        assertThat(Select.allChars()).hasToString("allChars()");
        assertThat(Select.allBooleans()).hasToString("allBooleans()");
        assertThat(Select.allBytes()).hasToString("allBytes()");
        assertThat(Select.allShorts()).hasToString("allShorts()");
        assertThat(Select.allInts()).hasToString("allInts()");
        assertThat(Select.allLongs()).hasToString("allLongs()");
        assertThat(Select.allFloats()).hasToString("allFloats()");
        assertThat(Select.allDoubles()).hasToString("allDoubles()");

        // with depth
        assertThat(Select.allInts().atDepth(5)).hasToString("allInts().atDepth(5)");
    }

    @Test
    void flatten() {
        final Selector selector = Select.allBooleans();
        final List<TargetSelector> results = ((Flattener) selector).flatten();
        assertThat(results).hasSize(2);
        SelectorAssert.assertSelector(results.get(0)).isClassSelectorWithNoScope().hasTargetClass(boolean.class);
        SelectorAssert.assertSelector(results.get(1)).isClassSelectorWithNoScope().hasTargetClass(Boolean.class);
    }

    @Test
    void toScope() {
        final Selector selector = Select.allInts();
        assertThatThrownBy(selector::toScope)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("method 'toScope()' is not supported for selector 'allInts()'");
    }

    @Test
    void depthValidation() {
        final Selector selector = Select.allInts();

        assertThatThrownBy(() -> selector.atDepth(-1))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("depth must not be negative: -1");
    }
}
