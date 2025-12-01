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
package org.instancio.internal.selectors;

import org.instancio.GetMethodSelector;
import org.instancio.internal.spi.InternalServiceProvider;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

public final class TargetGetterReference implements Target {

    private final GetMethodSelector<?, ?> selector;

    public TargetGetterReference(final GetMethodSelector<?, ?> selector) {
        this.selector = selector;
    }

    @Override
    public @Nullable Class<?> getTargetClass() {
        return null;
    }

    @Override
    public Target withRootClass(final TargetContext targetContext) {
        final MethodRef mr = MethodRef.from(selector);
        final Field field = resolveFieldFromGetterMethodReference(
                targetContext.getInternalServiceProviders(), mr.getTargetClass(), mr.getMethodName());
        return new TargetField(field);
    }

    /**
     * Resolves the field from the method reference selector.
     *
     * <p>For example, given {@code field(Person::getAge)},
     * the {@code declaringClass} would be {@code Person}
     * and {@code methodName} would be {@code getAge}.
     */
    @NonNull
    private Field resolveFieldFromGetterMethodReference(
            final List<InternalServiceProvider> internalServiceProviders,
            final Class<?> declaringClass,
            final String methodName) {

        for (InternalServiceProvider provider : internalServiceProviders) {
            final InternalServiceProvider.InternalGetterMethodFieldResolver resolver = provider.getGetterMethodFieldResolver();
            if (resolver == null) {
                continue;
            }

            final Field field = resolver.resolveField(declaringClass, methodName);
            if (field != null) {
                return field;
            }
        }

        throw Fail.withUsageError(ErrorMessageUtils.unableToResolveFieldFromMethodRef(declaringClass, methodName));
    }

    @Override
    public String toString() {
        final MethodRef mr = MethodRef.from(selector);
        return "field(" + mr.getTargetClass().getSimpleName() + "::" + mr.getMethodName() + ')';
    }
}
