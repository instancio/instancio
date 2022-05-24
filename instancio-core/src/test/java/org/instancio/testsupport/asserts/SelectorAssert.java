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
package org.instancio.testsupport.asserts;

import org.assertj.core.api.AbstractAssert;
import org.instancio.Scope;
import org.instancio.Selector;
import org.instancio.TargetSelector;
import org.instancio.internal.selectors.PrimitiveAndWrapperSelectorImpl;
import org.instancio.internal.selectors.SelectorGroupImpl;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.internal.selectors.SelectorTargetKind;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class SelectorAssert extends AbstractAssert<SelectorAssert, TargetSelector> {

    private SelectorAssert(final TargetSelector actual) {
        super(actual, SelectorAssert.class);
    }

    public static SelectorAssert assertSelector(final TargetSelector actual) {
        assertThat(actual).isNotNull();
        return new SelectorAssert(actual);
    }

    public <T extends TargetSelector> T getAs(final Class<T> type) {
        assertThat(type).isAssignableFrom(actual.getClass());
        return type.cast(actual);
    }

    public SelectorAssert hasTargetClass(Class<?> expected) {
        assertThat(getAs(SelectorImpl.class).getTargetClass()).isEqualTo(expected);
        return this;
    }

    public SelectorAssert hasNullTargetClass() {
        assertThat(getAs(SelectorImpl.class).getTargetClass()).isNull();
        return this;
    }

    public SelectorAssert hasNullField() {
        assertThat(getAs(SelectorImpl.class).getFieldName()).isNull();
        return this;
    }

    public SelectorAssert hasFieldName(String expected) {
        assertThat(getAs(SelectorImpl.class).getFieldName()).isEqualTo(expected);
        return this;
    }

    public SelectorAssert hasSelectorType(final SelectorTargetKind expected) {
        assertThat(getAs(SelectorImpl.class).getSelectorTargetKind()).isEqualTo(expected);
        return this;
    }

    public SelectorAssert hasScopeSize(final int expected) {
        assertThat(getAs(SelectorImpl.class).getScopes()).hasSize(expected);
        return this;
    }

    public List<Scope> getScopes() {
        return getAs(SelectorImpl.class).getScopes();
    }

    public SelectorAssert hasNoScope() {
        assertThat(getAs(SelectorImpl.class).getScopes()).isEmpty();
        return this;
    }

    public SelectorAssert isClassSelectorWithNoScope() {
        assertThat(getAs(SelectorImpl.class).getScopes()).isEmpty();
        return isClassSelector();
    }

    public SelectorAssert isClassSelector() {
        hasSelectorType(SelectorTargetKind.CLASS);
        return hasNullField();
    }

    public SelectorAssert isCoreTypeSelector(final Class<?> primitive, final Class<?> wrapper) {
        final PrimitiveAndWrapperSelectorImpl selector = getAs(PrimitiveAndWrapperSelectorImpl.class);
        assertThat(selector.flatten()).hasSize(2);
        assertSelector(selector.flatten().get(0))
                .isClassSelector()
                .hasTargetClass(primitive);
        assertSelector(selector.flatten().get(1))
                .isClassSelector()
                .hasTargetClass(wrapper);
        return this;
    }

    public SelectorAssert isCoreTypeSelectorWithoutScope(final Class<?> primitive, final Class<?> wrapper) {
        final PrimitiveAndWrapperSelectorImpl selector = getAs(PrimitiveAndWrapperSelectorImpl.class);
        assertThat(selector.flatten()).hasSize(2);
        assertSelector(selector.flatten().get(0))
                .isClassSelectorWithNoScope()
                .hasTargetClass(primitive);
        assertSelector(selector.flatten().get(1))
                .isClassSelectorWithNoScope()
                .hasTargetClass(wrapper);
        return this;
    }

    public SelectorAssert isSelectorGroupOfSize(final int expected) {
        assertThat(getAs(SelectorGroupImpl.class).getSelectors()).hasSize(expected);
        return this;
    }

    public List<Selector> getGroupedSelectors() {
        return getAs(SelectorGroupImpl.class).getSelectors();
    }
}