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
package org.instancio.internal.context;

import org.instancio.FilterPredicate;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * The filter predicate backing {@code withUnique()}. A dedicated type (rather than
 * a lambda) so that the engine can distinguish uniqueness filters from user-supplied
 * {@code filter()} predicates: values copied to assignment destinations are duplicates
 * by design and are exempt from uniqueness, but not from user filters.
 */
final class UniqueFilterPredicate implements FilterPredicate<@Nullable Object> {

    private final Set<@Nullable Object> generatedValues = new HashSet<>();

    @Override
    public boolean test(final @Nullable Object obj) {
        return generatedValues.add(obj);
    }
}
