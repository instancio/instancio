/*
 * Copyright 2022 the original author or authors.
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

import org.instancio.GroupableSelector;
import org.instancio.Selector;
import org.instancio.SelectorGroup;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public final class SelectorGroupImpl implements SelectorGroup, Flattener {

    private final List<GroupableSelector> selectors;

    public SelectorGroupImpl(final GroupableSelector... selectors) {
        this.selectors = Collections.unmodifiableList(Arrays.asList(selectors));
    }

    public List<Selector> getSelectors() {
        return selectors.stream().map(Selector.class::cast).collect(toList());
    }

    @Override
    public List<TargetSelector> flatten() {
        final List<TargetSelector> results = new ArrayList<>();

        for (GroupableSelector selector : selectors) {
            if (selector instanceof SelectorImpl) {
                results.add(selector);
            } else if (selector instanceof PrimitiveAndWrapperSelectorImpl) {
                results.addAll(((PrimitiveAndWrapperSelectorImpl) selector).flatten());
            } else {
                throw new InstancioException("Unhandled selector: " + selector.getClass());
            }
        }
        return results;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof SelectorGroupImpl)) return false;
        final SelectorGroupImpl that = (SelectorGroupImpl) o;
        return Objects.equals(getSelectors(), that.getSelectors());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSelectors());
    }

    @Override
    public String toString() {
        if (selectors.isEmpty()) {
            return "all()";
        }
        return String.format("all(%n%s%n)", selectors.stream()
                .map(it -> "\t" + it)
                .collect(joining("," + System.lineSeparator())));
    }
}
