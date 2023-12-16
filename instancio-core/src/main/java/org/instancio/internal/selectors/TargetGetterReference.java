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

import org.instancio.GetMethodSelector;

public final class TargetGetterReference implements Target {

    private final GetMethodSelector<?, ?> selector;

    public TargetGetterReference(final GetMethodSelector<?, ?> selector) {
        this.selector = selector;
    }

    @Override
    public Class<?> getTargetClass() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T, R> GetMethodSelector<T, R> getSelector() {
        return (GetMethodSelector<T, R>) selector;
    }

    @Override
    public String toString() {
        final MethodRef mr = MethodRef.from(selector);
        return "field(" + mr.getTargetClass().getSimpleName() + "::" + mr.getMethodName() + ')';
    }
}
