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

import org.instancio.GetMethodSelector;
import org.instancio.Scope;
import org.instancio.SetMethodSelector;
import org.instancio.TargetSelector;
import org.instancio.internal.ApiMethodSelector;
import org.instancio.internal.spi.InternalServiceProvider;
import org.instancio.internal.util.Verify;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SelectorProcessor {

    private final SetterSelectorHolder setMethodSelectorHolder;
    private final Target.TargetContext targetContext;

    public SelectorProcessor(
            final Class<?> rootClass,
            final List<InternalServiceProvider> internalServiceProviders,
            final SetterSelectorHolder setMethodSelectorHolder) {

        this.setMethodSelectorHolder = setMethodSelectorHolder;
        this.targetContext = new Target.TargetContext(rootClass, internalServiceProviders);
    }

    /**
     * Processing a selector involves a few things, including:
     *
     * <ul>
     *   <li>flattening selector groups</li>
     *   <li>setting root class on selectors where the class was not specified
     *       (e.g. {@code field("someField")})</li>
     *   <li>converting various types of {@link Target Targets}
     *       to {@link TargetClass}, {@link TargetField}, or {@link TargetSetter}.</li>
     * </ul>
     *
     * @param selector to process
     * @return a processed selector
     */
    public List<TargetSelector> process(
            @NotNull final TargetSelector selector,
            @NotNull final ApiMethodSelector apiMethodSelector) {

        if (selector instanceof SelectorImpl s) {
            final SelectorImpl result = processTargetAndScope(s, apiMethodSelector);
            return Collections.singletonList(result);

        } else if (selector instanceof SelectorGroupImpl selectorGroup) {
            return processGroup(selectorGroup, apiMethodSelector);

        } else if (selector instanceof GetMethodSelector<?, ?> getMethodSelector) {
            return process(SelectorImpl.builder()
                    .apiMethodSelector(apiMethodSelector)
                    .target(new TargetGetterReference(getMethodSelector))
                    .build(), apiMethodSelector);

        } else if (selector instanceof SetMethodSelector<?, ?>) {
            return process(SelectorImpl.builder()
                    .apiMethodSelector(apiMethodSelector)
                    .target(new TargetSetterReference((SetMethodSelector<?, ?>) selector))
                    .build(), apiMethodSelector);

        } else if (selector instanceof PrimitiveAndWrapperSelectorImpl ps) {
            final SelectorImpl.Builder primitiveBuilder = ps.getPrimitive().toBuilder().apiMethodSelector(apiMethodSelector);
            final SelectorImpl.Builder wrapperBuilder = ps.getWrapper().toBuilder().apiMethodSelector(apiMethodSelector);

            if (ps.isScoped()) {
                final List<Scope> scopes = createScopeWithRootClass(ps.getPrimitive().getScopes());

                return Arrays.asList(
                        primitiveBuilder.scopes(scopes).build(),
                        wrapperBuilder.scopes(scopes).build());
            }

            return Arrays.asList(primitiveBuilder.build(), wrapperBuilder.build());

        } else if (selector instanceof PredicateSelectorImpl ps) {
            final PredicateSelectorImpl processed = ps.toBuilder()
                    .apiMethodSelector(apiMethodSelector)
                    .scopes(createScopeWithRootClass(ps.getScopes()))
                    .build();

            return Collections.singletonList(processed);
        } else {
            // only remaining option is SelectorBuilder
            final SelectorBuilder builder = (SelectorBuilder) selector;
            return Collections.singletonList(builder.apiMethodSelector(apiMethodSelector).build());
        }
    }

    @NotNull
    private SelectorImpl processTargetAndScope(
            final SelectorImpl selector,
            final ApiMethodSelector apiMethodSelector) {

        if (selector.getTarget() instanceof TargetRoot) {
            return selector.toBuilder()
                    .apiMethodSelector(apiMethodSelector)
                    .build();
        }

        final List<Scope> processedScopes = createScopeWithRootClass(selector.getScopes());
        final Target target = selector.getTarget().withRootClass(targetContext);

        final SelectorImpl result = selector.toBuilder()
                .apiMethodSelector(apiMethodSelector)
                .target(target)
                .scopes(processedScopes)
                .build();

        if (result.getTarget() instanceof TargetSetter) {
            setMethodSelectorHolder.withSetterSelector(result);
        }

        return result;
    }

    private List<Scope> createScopeWithRootClass(final List<Scope> scopes) {
        if (scopes.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Scope> results = new ArrayList<>(scopes.size());
        for (Scope s : scopes) {
            if (s instanceof ScopeImpl scope) {
                final Target unprocessed = scope.getTarget();
                final Target processed = unprocessed.withRootClass(targetContext);
                results.add(new ScopeImpl(processed, scope.getDepth()));
            } else {
                Verify.isTrue(s instanceof PredicateScopeImpl, "expected predicate scope");
                results.add(s);
            }
        }
        return results;
    }

    private List<TargetSelector> processGroup(
            final SelectorGroupImpl selectorGroup,
            final ApiMethodSelector apiMethodSelector) {

        final List<TargetSelector> flattened = selectorGroup.flatten();
        final List<TargetSelector> results = new ArrayList<>(flattened.size());

        for (TargetSelector selector : flattened) {
            results.addAll(process(selector, apiMethodSelector));
        }
        return results;
    }
}
