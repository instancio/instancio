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
package org.instancio.internal.conditional;

import org.instancio.ConditionalValueOf;
import org.instancio.ConditionalValueOfRequiredAction;
import org.instancio.TargetSelector;
import org.instancio.When;

import java.util.function.Predicate;

public class InternalConditionalValueOf implements ConditionalValueOf {
    private final TargetSelector origin;

    public InternalConditionalValueOf(final TargetSelector origin) {
        this.origin = origin;
    }

    @Override
    public <T> ConditionalValueOfRequiredAction satisfies(final Predicate<T> originPredicate) {
        return new InternalConditionalValueOfRequiredAction(origin, originPredicate);
    }

    @Override
    public <T> ConditionalValueOfRequiredAction is(final T obj) {
        return new InternalConditionalValueOfRequiredAction(origin, When.is(obj));
    }

    @SafeVarargs
    @Override
    public final <T> ConditionalValueOfRequiredAction isIn(final T... values) {
        return new InternalConditionalValueOfRequiredAction(origin, When.isIn(values));
    }
}
