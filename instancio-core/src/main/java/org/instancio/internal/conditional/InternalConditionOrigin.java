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

import org.instancio.ConditionOrigin;
import org.instancio.RequiredConditionalAction;
import org.instancio.TargetSelector;
import org.instancio.internal.ApiValidator;

import java.util.Objects;
import java.util.function.Predicate;

public class InternalConditionOrigin implements ConditionOrigin {
    private final TargetSelector origin;

    public InternalConditionOrigin(final TargetSelector origin) {
        this.origin = ApiValidator.notNull(origin, "origin selector must not be null");
    }

    @Override
    public <T> RequiredConditionalAction satisfies(final Predicate<T> originPredicate) {
        return new RequiredConditionalActionImpl(origin, originPredicate);
    }

    @Override
    public <T> RequiredConditionalAction is(final T obj) {
        return new RequiredConditionalActionImpl(origin, o -> Objects.equals(o, obj));
    }

    @SafeVarargs
    @Override
    public final <T> RequiredConditionalAction isIn(final T... values) {
        return new RequiredConditionalActionImpl(origin, o -> {
            for (T val : values) {
                if (Objects.equals(o, val)) {
                    return true;
                }
            }
            return false;
        });
    }
}
