/*
 * Copyright 2022-2025 the original author or authors.
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

import org.instancio.SetMethodSelector;
import org.instancio.internal.util.ReflectionUtils;

import java.lang.reflect.Method;

public final class TargetSetterReference implements Target {

    private final SetMethodSelector<?, ?> selector;

    public TargetSetterReference(final SetMethodSelector<?, ?> selector) {
        this.selector = selector;
    }

    @Override
    public Class<?> getTargetClass() {
        return null;
    }

    @Override
    public Target withRootClass(final TargetContext targetContext) {
        final MethodRef mr = MethodRef.from(selector);

        // Match method by name only since we can't extract
        // the parameter type from the method reference
        final Method method = ReflectionUtils.getSetterMethod(
                mr.getTargetClass(), mr.getMethodName(), null);

        return new TargetSetter(method);
    }

    @Override
    public String toString() {
        final MethodRef mr = MethodRef.from(selector);
        return "setter(" + mr.getTargetClass().getSimpleName() + "::" + mr.getMethodName() + ')';
    }
}
