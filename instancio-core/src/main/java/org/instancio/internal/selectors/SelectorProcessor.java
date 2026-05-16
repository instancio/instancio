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
import org.instancio.Scope;
import org.instancio.SetMethodSelector;
import org.instancio.TargetSelector;
import org.instancio.internal.ApiMethodSelector;
import org.instancio.internal.spi.InternalExtension;
import org.instancio.internal.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public final class SelectorProcessor {

    private final SetterSelectorHolder setMethodSelectorHolder;
    private final Target.TargetContext targetContext;

    public SelectorProcessor(
            final Class<?> rootClass,
            final List<InternalExtension> internalExtensions,
            final SetterSelectorHolder setMethodSelectorHolder) {

        this.setMethodSelectorHolder = setMethodSelectorHolder;
        this.targetContext = new Target.TargetContext(rootClass, internalExtensions);
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
            final TargetSelector selector,
            final ApiMethodSelector apiMethodSelector) {

        if (selector instanceof PredicateSelectorImpl ps) {
            final PredicateSelectorImpl processed = processPredicateSelectorTargetAndScope(ps, apiMethodSelector);
            return List.of(processed);

        } else if (selector instanceof SelectorGroupImpl selectorGroup) {
            return processGroup(selectorGroup, apiMethodSelector);

        } else if (selector instanceof GetMethodSelector<?, ?> getMethodSelector) {
            return process(PredicateSelectorImpl.builder()
                    .target(new TargetGetterReference(getMethodSelector))
                    .apiMethodSelector(apiMethodSelector)
                    .build(), apiMethodSelector);

        } else if (selector instanceof SetMethodSelector<?, ?>) {
            return process(PredicateSelectorImpl.builder()
                    .target(new TargetSetterReference((SetMethodSelector<?, ?>) selector))
                    .apiMethodSelector(apiMethodSelector)
                    .build(), apiMethodSelector);

        } else {
            // only remaining option is SelectorBuilder
            final SelectorBuilder builder = (SelectorBuilder) selector;
            return List.of(builder.apiMethodSelector(apiMethodSelector).build());
        }
    }

    private PredicateSelectorImpl processPredicateSelectorTargetAndScope(
            final PredicateSelectorImpl selector,
            final ApiMethodSelector apiMethodSelector) {

        final List<Scope> processedScopes = createScopeWithRootClass(selector.getScopes());

        final PredicateSelectorImpl result = selector.toBuilderWithContext(targetContext)
                .apiMethodSelector(apiMethodSelector)
                .scopes(processedScopes)
                .build();

        if (result.getTarget() instanceof TargetSetter) {
            setMethodSelectorHolder.withSetterSelector(result);
        }

        return result;
    }

    private List<Scope> createScopeWithRootClass(final List<Scope> scopes) {
        if (scopes.isEmpty()) {
            return List.of();
        }

        final List<Scope> results = new ArrayList<>(scopes.size());

        for (Scope s : scopes) {
            final PredicateScopeImpl scope = (PredicateScopeImpl) s;
            final PredicateSelectorImpl selector = (PredicateSelectorImpl) scope.getPredicateSelector();
            final PredicateSelectorImpl processedSelector = selector.toBuilderWithContext(targetContext).build();

            final String scopeDescription = ObjectUtils.defaultIfNull(
                    scope.getApiInvocationDescription(),
                    "scope(%s)".formatted(processedSelector.getApiInvocationDescription())); // TODO refactor description handling

            results.add(new PredicateScopeImpl(processedSelector, scopeDescription));
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
