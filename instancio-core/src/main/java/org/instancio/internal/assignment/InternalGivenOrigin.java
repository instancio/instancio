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
package org.instancio.internal.assignment;

import org.instancio.GivenOrigin;
import org.instancio.GivenOriginPredicate;
import org.instancio.TargetSelector;
import org.instancio.When;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public class InternalGivenOrigin implements GivenOrigin {
    private final TargetSelector origin;

    public InternalGivenOrigin(final TargetSelector origin) {
        this.origin = origin;
    }

    @Override
    public <T extends @Nullable Object> GivenOriginPredicate satisfies(final Predicate<T> originPredicate) {
        return new InternalGivenOriginPredicateRequiredAction(origin, originPredicate);
    }

    @Override
    public <T extends @Nullable Object> GivenOriginPredicate is(final T obj) {
        return new InternalGivenOriginPredicateRequiredAction(origin, When.is(obj));
    }

    @Override
    public <T extends @Nullable Object> GivenOriginPredicate isNot(final T obj) {
        return new InternalGivenOriginPredicateRequiredAction(origin, When.isNot(obj));
    }

    @SafeVarargs
    @Override
    public final <T extends @Nullable Object> GivenOriginPredicate isIn(final T... values) {
        return new InternalGivenOriginPredicateRequiredAction(origin, When.isIn(values));
    }
}
