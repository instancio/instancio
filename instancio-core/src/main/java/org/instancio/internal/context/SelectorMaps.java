/*
 * Copyright 2022-2024 the original author or authors.
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

import org.instancio.Assignment;
import org.instancio.GeneratorSpecProvider;
import org.instancio.OnCompleteCallback;
import org.instancio.TargetSelector;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.assignment.InternalAssignment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.instancio.internal.context.SetModelSelectorHelper.applyModelSelectorScopes;

public final class SelectorMaps {

    private final AssignmentSelectorMap assignmentSelectorMap;
    private final GeneratorSelectorMap generatorSelectorMap;
    private final BooleanSelectorMap ignoreSelectorMap = new BooleanSelectorMap();
    private final BooleanSelectorMap withNullableSelectorMap = new BooleanSelectorMap();
    private final ModelContextSelectorMap setModelSelectorMap = new ModelContextSelectorMap();
    private final OnCompleteCallbackSelectorMap onCompleteSelectorMap = new OnCompleteCallbackSelectorMap();
    private final SubtypeSelectorMap subtypeSelectorMap = new SubtypeSelectorMap();

    SelectorMaps(final GeneratorContext generatorContext) {
        this.assignmentSelectorMap = new AssignmentSelectorMap(generatorContext);
        this.generatorSelectorMap = new GeneratorSelectorMap(generatorContext);
    }

    void initSelectorMaps(final ModelContextSource contextSource) {
        // Before initialising remaining selector maps, update this model
        // with selectors from other models provided via setModel()
        for (Map.Entry<TargetSelector, ModelContext<?>> entry : contextSource.getSetModelMap().entrySet()) {
            copyToThisContext(entry.getKey(), entry.getValue());
        }

        setModelSelectorMap.putAll(contextSource.getSetModelMap());
        ignoreSelectorMap.putAll(contextSource.getIgnoreSet());
        withNullableSelectorMap.putAll(contextSource.getWithNullableSet());
        onCompleteSelectorMap.putAll(contextSource.getOnCompleteMap());
        generatorSelectorMap.putAllGeneratorSpecs(contextSource.getGeneratorSpecMap());
        generatorSelectorMap.putAllGenerators(contextSource.getGeneratorMap());
        assignmentSelectorMap.putAll(contextSource.getAssignmentMap());

        // subtype map must be last as it needs subtypes from assignment/generator spec subtypes
        subtypeSelectorMap.putAll(contextSource.getSubtypeMap());
        subtypeSelectorMap.putAll(generatorSelectorMap.getSubtypeMap());
        subtypeSelectorMap.putAll(assignmentSelectorMap.getSubtypeMap());
    }

    @SuppressWarnings("PMD.NPathComplexity")
    private void copyToThisContext(final TargetSelector modelTarget, final ModelContext<?> otherCtx) {
        final ModelContextSource src = otherCtx.getContextSource();

        for (Map.Entry<TargetSelector, Generator<?>> entry : src.getGeneratorMap().entrySet()) {
            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, entry.getKey());
            generatorSelectorMap.putGenerator(resolvedSelector, entry.getValue());
        }

        for (Map.Entry<TargetSelector, GeneratorSpecProvider<?>> entry : src.getGeneratorSpecMap().entrySet()) {
            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, entry.getKey());
            generatorSelectorMap.putGeneratorSpec(resolvedSelector, entry.getValue());
        }

        for (TargetSelector ignoredTarget : src.getIgnoreSet()) {
            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, ignoredTarget);
            ignoreSelectorMap.add(resolvedSelector);
        }

        for (TargetSelector nullableTarget : src.getWithNullableSet()) {
            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, nullableTarget);
            withNullableSelectorMap.add(resolvedSelector);
        }

        for (Map.Entry<TargetSelector, OnCompleteCallback<?>> entry : src.getOnCompleteMap().entrySet()) {
            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, entry.getKey());
            onCompleteSelectorMap.put(resolvedSelector, entry.getValue());
        }

        for (Map.Entry<TargetSelector, List<Assignment>> entry : src.getAssignmentMap().entrySet()) {
            final TargetSelector selector = entry.getKey();
            final List<Assignment> assignments = entry.getValue();
            final List<Assignment> updatedAssignments = new ArrayList<>(assignments.size());

            for (Assignment assignment : assignments) {
                final InternalAssignment a = (InternalAssignment) assignment;
                final TargetSelector updatedOrigin = applyModelSelectorScopes(modelTarget, a.getOrigin());
                final TargetSelector updatedDestination = applyModelSelectorScopes(modelTarget, a.getDestination());
                final InternalAssignment updatedAssignment = a.toBuilder()
                        .origin(updatedOrigin)
                        .destination(updatedDestination)
                        .build();

                updatedAssignments.add(updatedAssignment);
            }

            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, selector);
            assignmentSelectorMap.put(resolvedSelector, updatedAssignments);
        }

        for (Map.Entry<TargetSelector, Class<?>> entry : src.getSubtypeMap().entrySet()) {
            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, entry.getKey());
            subtypeSelectorMap.put(resolvedSelector, entry.getValue());
        }

        for (Map.Entry<TargetSelector, ModelContext<?>> entry : src.getSetModelMap().entrySet()) {
            final TargetSelector resolvedSelector = applyModelSelectorScopes(modelTarget, entry.getKey());
            setModelSelectorMap.put(resolvedSelector, entry.getValue());

            copyToThisContext(resolvedSelector, entry.getValue()); // recurse
        }
    }


    AssignmentSelectorMap getAssignmentSelectorMap() {
        return assignmentSelectorMap;
    }

    GeneratorSelectorMap getGeneratorSelectorMap() {
        return generatorSelectorMap;
    }

    BooleanSelectorMap getIgnoreSelectorMap() {
        return ignoreSelectorMap;
    }

    BooleanSelectorMap getWithNullableSelectorMap() {
        return withNullableSelectorMap;
    }

    ModelContextSelectorMap getSetModelSelectorMap() {
        return setModelSelectorMap;
    }

    OnCompleteCallbackSelectorMap getOnCompleteSelectorMap() {
        return onCompleteSelectorMap;
    }

    SubtypeSelectorMap getSubtypeSelectorMap() {
        return subtypeSelectorMap;
    }

    public boolean allEmpty() {
        return !hasAssignments()
                && !hasGenerators()
                && !hasCallbacks()
                && !hasSetModels()
                && ignoreSelectorMap.getSelectorMap().isEmpty()
                && withNullableSelectorMap.getSelectorMap().isEmpty()
                && subtypeSelectorMap.getSelectorMap().isEmpty();
    }

    public boolean hasGenerators() {
        return !generatorSelectorMap.getSelectorMap().isEmpty();
    }

    public boolean hasCallbacks() {
        return !onCompleteSelectorMap.getSelectorMap().isEmpty();
    }

    public boolean hasAssignments() {
        return !assignmentSelectorMap.getOriginSelectors().getSelectorMap().isEmpty();
    }

    public boolean hasSetModels() {
        return !setModelSelectorMap.getSelectorMap().isEmpty();
    }
}
