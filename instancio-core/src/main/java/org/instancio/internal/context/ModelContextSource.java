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
import org.instancio.feed.Feed;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.util.Sonar;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.instancio.internal.util.CollectionUtils.asUnmodifiableList;
import static org.instancio.internal.util.CollectionUtils.asUnmodifiableMap;
import static org.instancio.internal.util.CollectionUtils.asUnmodifiableSet;

@SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
final class ModelContextSource {

    private final List<Type> withTypeParametersList;
    private final Map<TargetSelector, Class<?>> subtypeMap;
    private final Map<TargetSelector, GeneratorSpecProvider<?>> generatorSpecMap;
    private final Map<TargetSelector, Generator<?>> generatorMap;
    private final Map<TargetSelector, OnCompleteCallback<?>> onCompleteMap;
    private final Map<TargetSelector, Predicate<?>> filterMap;
    private final Map<TargetSelector, List<Assignment>> assignmentMap;
    private final Map<TargetSelector, ModelContext> setModelMap;
    private final Map<TargetSelector, Function<GeneratorContext, Feed>> feedMap;
    private final Set<TargetSelector> ignoreSet;
    private final Set<TargetSelector> withNullableSet;

    @SuppressWarnings({Sonar.TOO_MANY_PARAMETERS, "PMD.ExcessiveParameterList"})
    ModelContextSource(
            final List<Type> withTypeParametersList,
            final Map<TargetSelector, Class<?>> subtypeSelectors,
            final Map<TargetSelector, GeneratorSpecProvider<?>> generatorSpecMap,
            final Map<TargetSelector, Generator<?>> generatorMap,
            final Map<TargetSelector, OnCompleteCallback<?>> onCompleteMap,
            final Map<TargetSelector, Predicate<?>> filterMap,
            final Map<TargetSelector, List<Assignment>> assignmentMap,
            final Map<TargetSelector, ModelContext> setModelMap,
            final Map<TargetSelector, Function<GeneratorContext, Feed>> feedMap,
            final Set<TargetSelector> ignoreSet,
            final Set<TargetSelector> withNullableSet) {

        this.withTypeParametersList = asUnmodifiableList(withTypeParametersList);
        this.subtypeMap = asUnmodifiableMap(subtypeSelectors);
        this.generatorSpecMap = asUnmodifiableMap(generatorSpecMap);
        this.generatorMap = asUnmodifiableMap(generatorMap);
        this.onCompleteMap = asUnmodifiableMap(onCompleteMap);
        this.filterMap = asUnmodifiableMap(filterMap);
        this.assignmentMap = asUnmodifiableMap(assignmentMap);
        this.setModelMap = asUnmodifiableMap(setModelMap);
        this.feedMap = asUnmodifiableMap(feedMap);
        this.ignoreSet = asUnmodifiableSet(ignoreSet);
        this.withNullableSet = asUnmodifiableSet(withNullableSet);
    }

    List<Type> getWithTypeParametersList() {
        return withTypeParametersList;
    }

    Map<TargetSelector, Class<?>> getSubtypeMap() {
        return subtypeMap;
    }

    Map<TargetSelector, GeneratorSpecProvider<?>> getGeneratorSpecMap() {
        return generatorSpecMap;
    }

    Map<TargetSelector, Generator<?>> getGeneratorMap() {
        return generatorMap;
    }

    Map<TargetSelector, OnCompleteCallback<?>> getOnCompleteMap() {
        return onCompleteMap;
    }

    Map<TargetSelector, Predicate<?>> getFilterMap() {
        return filterMap;
    }

    Map<TargetSelector, List<Assignment>> getAssignmentMap() {
        return assignmentMap;
    }

    Map<TargetSelector, ModelContext> getSetModelMap() {
        return setModelMap;
    }

    Map<TargetSelector, Function<GeneratorContext, Feed>> getFeedMap() {
        return feedMap;
    }

    Set<TargetSelector> getIgnoreSet() {
        return ignoreSet;
    }

    Set<TargetSelector> getWithNullableSet() {
        return withNullableSet;
    }
}
