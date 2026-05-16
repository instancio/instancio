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
package org.instancio.testsupport.asserts;

import org.assertj.core.api.AbstractAssert;
import org.instancio.PredicateSelector;
import org.instancio.Scope;
import org.instancio.TargetSelector;
import org.instancio.internal.selectors.InternalSelector;
import org.instancio.internal.selectors.PredicateSelectorImpl;
import org.instancio.internal.selectors.Target;
import org.instancio.internal.selectors.TargetClass;
import org.instancio.internal.selectors.TargetField;
import org.instancio.internal.selectors.TargetRoot;
import org.instancio.internal.selectors.TargetSetter;

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
        assertThat(getAs(PredicateSelectorImpl.class).getTarget().getTargetClass()).isEqualTo(expected);
        return this;
    }

    public SelectorAssert hasNullField() {
        return hasFieldName(null);
    }

    public SelectorAssert hasFieldName(String expected) {
        final Target target = getAs(PredicateSelectorImpl.class).getTarget();
        assertThat(((TargetField) target).getField().getName()).isEqualTo(expected);
        return this;
    }

    public SelectorAssert hasMethodName(String expected) {
        final Target target = getAs(PredicateSelectorImpl.class).getTarget();
        assertThat(((TargetSetter) target).getSetter().getName()).isEqualTo(expected);
        return this;
    }

    public SelectorAssert isFieldSelector() {
        return hasTargetOfType(TargetField.class);
    }

    public SelectorAssert isSetterSelector() {
        return hasTargetOfType(TargetSetter.class);
    }

    public SelectorAssert isClassSelector() {
        return hasTargetOfType(TargetClass.class);
    }

    private SelectorAssert hasTargetOfType(final Class<?> type) {
        assertThat(getAs(PredicateSelectorImpl.class).getTarget()).isExactlyInstanceOf(type);
        return this;
    }

    public SelectorAssert isRootSelector() {
        final PredicateSelectorImpl selector = getAs(PredicateSelectorImpl.class);
        assertThat(selector.getTarget()).isExactlyInstanceOf(TargetRoot.class);
        assertThat(selector.getTarget().getTargetClass()).isNull();
        assertThat(selector.getScopes()).isEmpty();
        assertThat(selector.getStackTraceHolder()).isNotNull();
        return this;
    }

    public SelectorAssert hasScopeSize(final int expected) {
        assertThat(getAs(InternalSelector.class).getScopes()).hasSize(expected);
        return this;
    }

    public List<Scope> getScopes() {
        return getAs(InternalSelector.class).getScopes();
    }

    public SelectorAssert hasNoScope() {
        assertThat(getAs(InternalSelector.class).getScopes()).isEmpty();
        return this;
    }

    public SelectorAssert isPredicateSelector() {
        assertThat(actual).isInstanceOf(PredicateSelector.class);
        return this;
    }

    public SelectorAssert isClassSelectorWithNoScope() {
        assertThat(getAs(InternalSelector.class).getScopes()).isEmpty();
        return isClassSelector();
    }
}