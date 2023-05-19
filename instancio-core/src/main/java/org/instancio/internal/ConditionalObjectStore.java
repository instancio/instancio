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
package org.instancio.internal;

import org.instancio.TargetSelector;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * A store for keeping track of generated values for destination
 * selectors of conditionals.
 */
public class ConditionalObjectStore implements GenerationListener {
    private static final Logger LOG = LoggerFactory.getLogger(ConditionalObjectStore.class);

    /**
     * The inner Map maps generated objects to destination selectors.
     * The outer map groups inner maps using "scope" objects.
     * The grouping is needed for handling collections of POJOs or records.
     * For example, given:
     *
     * <pre>{@code
     *   List<Person> persons = Instancio.ofList(Person.class)
     *       .when(valueOf(Person::gender).is(Gender.F).set(field(Person::name), "Jane")
     *       .when(valueOf(Person::gender).is(Gender.M).set(field(Person::name), "John")
     *       .create();
     * }</pre>
     *
     * <p>each generated {@code Person} object will have a mapping of
     * {@code field(Person::name)} to the corresponding name specified
     * in the conditional.
     */
    private final Map<Object, Map<TargetSelector, List<Object>>> objectStore = new IdentityHashMap<>();

    private final Deque<Object> scopes = new ArrayDeque<>();

    private final ModelContext<?> context;

    ConditionalObjectStore(final ModelContext<?> context) {
        this.context = context;
    }

    void enterScope() {
        enterScope(new Object());
    }

    void enterScope(Object e) {
        scopes.push(e);
        LOG.trace("--> Enter scope {}", e);
    }

    void exitScope() {
        final Object e = scopes.pop();

        objectStore.remove(e);
        LOG.trace("<-- Exit scope {}", e);
    }

    @Override
    public void objectCreated(final InternalNode node, final GeneratorResult result) {
        final List<TargetSelector> destinationSelector = context.getConditionalDestinationSelectors(node);

        for (TargetSelector destination : destinationSelector) {
            putValue(destination, result.getValue());
            LOG.debug("Added {} for {}", result, destination);
        }
    }

    /**
     * Returns candidate values for the specified destination selector.
     *
     * @param destination for which to return values
     * @return candidate values
     */
    public List<Object> getValues(final TargetSelector destination) {
        final Object scope = scopes.peekLast();
        final Map<TargetSelector, List<Object>> destinationValues = objectStore.get(scope);

        if (destinationValues == null) {
            return Collections.emptyList();
        }
        final List<Object> results = destinationValues.get(destination);

        if (results == null) {
            return Collections.emptyList();
        }

        return results;
    }

    private void putValue(final TargetSelector selector, final Object generatedValue) {
        final Object scope = scopes.peekLast();

        final Map<TargetSelector, List<Object>> destinationValues =
                objectStore.computeIfAbsent(scope, k -> new IdentityHashMap<>());

        final List<Object> values = destinationValues.computeIfAbsent(selector, k -> new ArrayList<>());
        values.add(generatedValue);
    }
}
