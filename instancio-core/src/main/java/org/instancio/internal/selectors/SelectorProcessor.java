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
import org.instancio.GroupableSelector;
import org.instancio.Scope;
import org.instancio.SetMethodSelector;
import org.instancio.TargetSelector;
import org.instancio.internal.Flattener;
import org.instancio.internal.spi.InternalServiceProvider;
import org.instancio.internal.spi.InternalServiceProvider.InternalGetterMethodFieldResolver;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SelectorProcessor {

    private final Class<?> rootClass;
    private final List<InternalServiceProvider> internalServiceProviders;
    private final SetterSelectorHolder setMethodSelectorHolder;

    public SelectorProcessor(
            final Class<?> rootClass,
            final List<InternalServiceProvider> internalServiceProviders,
            final SetterSelectorHolder setMethodSelectorHolder) {

        this.rootClass = rootClass;
        this.internalServiceProviders = internalServiceProviders;
        this.setMethodSelectorHolder = setMethodSelectorHolder;
    }

    /**
     * This method converts various types of selector {@link Target Targets}
     * to {@link TargetClass}, {@link TargetField}, or {@link TargetSetter}.
     *
     * @param selector to process
     * @return a processed selector
     */
    public List<TargetSelector> process(@NotNull final TargetSelector selector) {
        if (selector instanceof SelectorImpl) {
            final SelectorImpl result = processTargetAndScope((SelectorImpl) selector);
            return Collections.singletonList(result);

        } else if (selector instanceof SelectorGroupImpl) {
            return processGroup((SelectorGroupImpl) selector);

        } else if (selector instanceof GetMethodSelector<?, ?>) {
            return process(SelectorImpl.builder()
                    .target(new TargetGetterReference((GetMethodSelector<?, ?>) selector))
                    .build());

        } else if (selector instanceof SetMethodSelector<?, ?>) {
            return process(SelectorImpl.builder()
                    .target(new TargetSetterReference((SetMethodSelector<?, ?>) selector))
                    .build());

        } else if (selector instanceof PrimitiveAndWrapperSelectorImpl) {
            final PrimitiveAndWrapperSelectorImpl ps = (PrimitiveAndWrapperSelectorImpl) selector;
            if (ps.isScoped()) {
                final List<Scope> scopes = createScopeWithRootClass(ps.getPrimitive().getScopes());

                return Arrays.asList(
                        ps.getPrimitive().toBuilder().scopes(scopes).build(),
                        ps.getWrapper().toBuilder().scopes(scopes).build());
            }

            return Arrays.asList(ps.getPrimitive(), ps.getWrapper());

        } else if (selector instanceof PredicateSelectorImpl) {
            // No processing required for predicate selectors.
            // They don't support scopes and root class is not applicable to predicates.
            return Collections.singletonList(selector);

        } else {
            // only remaining option is SelectorBuilder
            final SelectorBuilder builder = (SelectorBuilder) selector;
            return Collections.singletonList(builder.build());
        }
    }

    @NotNull
    private SelectorImpl processTargetAndScope(final SelectorImpl selector) {
        if (selector.getTarget() instanceof TargetRoot) {
            return selector;
        }

        final List<Scope> processedScopes = createScopeWithRootClass(selector.getScopes());
        final Target target = createTargetWithRootClass(selector.getTarget());

        final SelectorImpl result = selector.toBuilder()
                .target(target)
                .scopes(processedScopes)
                .build();

        if (result.getTarget() instanceof TargetSetter) {
            setMethodSelectorHolder.withSetterSelector(result);
        }

        return result;
    }

    @NotNull
    private Target createTargetWithRootClass(final Target target) {
        final Target result;

        if (target instanceof TargetFieldName) {
            final TargetFieldName t = (TargetFieldName) target;
            final Class<?> targetClass = ObjectUtils.defaultIfNull(t.getTargetClass(), rootClass);
            final Field field = ReflectionUtils.getFieldOrNull(targetClass, t.getFieldName());

            if (field == null) {
                throw Fail.withUsageError(String.format("invalid field '%s' for %s", t.getFieldName(), targetClass));
            }

            result = new TargetField(field);
        } else if (target instanceof TargetGetterReference) {
            final TargetGetterReference t = (TargetGetterReference) target;
            final MethodRef mr = MethodRef.from(t.getSelector());
            final Field field = resolveFieldFromGetterMethodReference(mr.getTargetClass(), mr.getMethodName());

            result = new TargetField(field);
        } else if (target instanceof TargetSetterReference) {
            final TargetSetterReference t = (TargetSetterReference) target;
            final MethodRef mr = MethodRef.from(t.getSelector());

            // Match method by name only since we can't extract
            // the parameter type from the method reference
            final Method method = ReflectionUtils.getSetterMethod(
                    mr.getTargetClass(), mr.getMethodName(), null);

            result = new TargetSetter(method);
        } else if (target instanceof TargetSetterName) {
            final TargetSetterName t = (TargetSetterName) target;
            final Class<?> targetClass = ObjectUtils.defaultIfNull(t.getTargetClass(), rootClass);

            final Method method = ReflectionUtils.getSetterMethod(
                    targetClass, t.getMethodName(), t.getParameterType());

            result = new TargetSetter(method);
        } else if (target instanceof TargetClass) {
            // only remaining option is TargetClass
            final TargetClass t = (TargetClass) target;
            result = new TargetClass(t.getTargetClass());
        } else {
            result = target; // given target doesn't need to be processed
        }

        return result;
    }

    /**
     * Resolves the field from the method reference selector.
     *
     * <p>For example, given {@code field(Person::getAge)},
     * the {@code declaringClass} would be {@code Person}
     * and {@code methodName} would be {@code getAge}.
     */
    @NotNull
    private Field resolveFieldFromGetterMethodReference(final Class<?> declaringClass, final String methodName) {
        for (InternalServiceProvider provider : internalServiceProviders) {
            final InternalGetterMethodFieldResolver resolver = provider.getGetterMethodFieldResolver();
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

    private List<Scope> createScopeWithRootClass(final List<Scope> scopes) {
        if (scopes.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Scope> results = new ArrayList<>(scopes.size());
        for (Scope s : scopes) {
            final ScopeImpl scope = (ScopeImpl) s;
            final Target unprocessed = scope.getTarget();
            final Target processed = createTargetWithRootClass(unprocessed);
            results.add(new ScopeImpl(processed, scope.getDepth()));
        }
        return results;
    }

    private List<TargetSelector> processGroup(final SelectorGroupImpl selectorGroup) {
        final List<TargetSelector> results = new ArrayList<>();

        for (GroupableSelector groupMember : selectorGroup.getSelectors()) {

            if (groupMember instanceof Flattener<?>) {
                final List<TargetSelector> flattened = ((Flattener<TargetSelector>) groupMember).flatten();
                for (TargetSelector selector : flattened) {
                    results.addAll(process(selector));
                }
            } else {
                // only remaining option is SelectorBuilder
                final SelectorBuilder builder = (SelectorBuilder) groupMember;
                results.add(builder.build());
            }
        }
        return results;
    }

}
