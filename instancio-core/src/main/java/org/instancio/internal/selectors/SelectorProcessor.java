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
import org.instancio.internal.ApiMethod;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.spi.InternalExtension;
import org.instancio.internal.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves, expands, and flattens the heterogeneous raw API selector types
 * (groups, elementOf, getter/setter references, builders) into a uniform
 * list of {@link PredicateSelectorImpl} in canonical internal form. This is
 * the single entry point for normalizing a selector before the engine uses it.
 */
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
    public List<PredicateSelectorImpl> process(
            final TargetSelector selector,
            final ApiMethod apiMethod) {

        if (selector instanceof ElementOfSelectorImpl eos) {
            return processElementOfSelector(eos, apiMethod);

        } else if (selector instanceof PredicateSelectorImpl ps) {
            final PredicateSelectorImpl processed = processPredicateSelectorTargetAndScope(ps, apiMethod);
            return List.of(processed);

        } else if (selector instanceof SelectorGroupImpl selectorGroup) {
            return processGroup(selectorGroup, apiMethod);

        } else if (selector instanceof GetMethodSelector<?, ?> getMethodSelector) {
            return process(PredicateSelectorImpl.builder()
                    .target(new TargetGetterReference(getMethodSelector))
                    .apiMethod(apiMethod)
                    .build(), apiMethod);

        } else if (selector instanceof SetMethodSelector<?, ?>) {
            return process(PredicateSelectorImpl.builder()
                    .target(new TargetSetterReference((SetMethodSelector<?, ?>) selector))
                    .apiMethod(apiMethod)
                    .build(), apiMethod);

        } else {
            // only remaining option is SelectorBuilder; recurse so the resulting
            final SelectorBuilder builder = (SelectorBuilder) selector;
            return process(builder.apiMethod(apiMethod).build(), apiMethod);
        }
    }

    private List<PredicateSelectorImpl> processElementOfSelector(
            final ElementOfSelectorImpl eos,
            final ApiMethod apiMethod) {

        final List<PredicateSelectorImpl> processedContainerSelectors =
                process(eos.getContainerSelector(), ApiMethod.NONE);

        ApiValidator.isTrue(processedContainerSelectors.size() == 1,
                "elementOf() does not support container selectors that expand into multiple targets: %s", eos);

        final TargetSelector innerSelector = eos.getInnerSelector();

        final List<PredicateSelectorImpl> innerSelectors = innerSelector == null
                ? List.of()
                : process(innerSelector, ApiMethod.NONE);

        return ElementOfSelectorBaker.bake(eos, apiMethod, processedContainerSelectors.get(0), innerSelectors);
    }

    private PredicateSelectorImpl processPredicateSelectorTargetAndScope(
            final PredicateSelectorImpl selector,
            final ApiMethod apiMethod) {

        final List<Scope> processedScopes = createScopeWithRootClass(selector.getScopes());

        final PredicateSelectorImpl result = selector.toBuilderWithContext(targetContext)
                .apiMethod(apiMethod)
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

    private List<PredicateSelectorImpl> processGroup(
            final SelectorGroupImpl selectorGroup,
            final ApiMethod apiMethod) {

        final List<TargetSelector> flattened = selectorGroup.flatten();
        final List<PredicateSelectorImpl> results = new ArrayList<>(flattened.size());

        for (TargetSelector selector : flattened) {
            results.addAll(process(selector, apiMethod));
        }
        return results;
    }
}
