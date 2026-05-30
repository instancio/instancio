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
import org.instancio.Scope;
import org.instancio.internal.selectors.PredicateScopeImpl;
import org.instancio.internal.selectors.PredicateSelectorImpl;
import org.instancio.internal.selectors.Target;
import org.instancio.internal.selectors.TargetClass;
import org.instancio.internal.selectors.TargetField;
import org.jspecify.annotations.Nullable;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class ScopeAssert extends AbstractAssert<ScopeAssert, Scope> {

    private ScopeAssert(final Scope actual) {
        super(actual, ScopeAssert.class);
    }

    public static ScopeAssert assertScope(final Scope actual) {
        assertThat(actual).isNotNull();
        return new ScopeAssert(actual);
    }

    public ScopeAssert hasTargetClass(final Class<?> expected) {
        final Target target = getTarget();

        if (target instanceof TargetField tf) {
            assertThat(tf.getTargetClass()).isEqualTo(expected);
        } else {
            assertThat(((TargetClass) target).getTargetClass()).isEqualTo(expected);
        }
        return this;
    }

    public ScopeAssert hasField(final String expected) {
        final Target target = getTarget();
        assertThat(((TargetField) target).getField().getName()).isEqualTo(expected);
        return this;
    }

    public ScopeAssert isPredicateScope() {
        assertThat(actual).isExactlyInstanceOf(PredicateScopeImpl.class);
        return this;
    }

    public ScopeAssert isFieldSelector() {
        return isOfType(TargetField.class);
    }

    public ScopeAssert isClassSelector() {
        return isOfType(TargetClass.class);
    }

    private ScopeAssert isOfType(final Class<?> type) {
        assertThat(getTarget()).isExactlyInstanceOf(type);
        return this;
    }

    @Nullable
    private Target getTarget() {
        final PredicateScopeImpl scope = getAs(PredicateScopeImpl.class);
        return ((PredicateSelectorImpl) scope.getPredicateSelector()).getTarget();
    }

    private <T extends Scope> T getAs(final Class<T> type) {
        assertThat(type).isAssignableFrom(actual.getClass());
        return type.cast(actual);
    }
}