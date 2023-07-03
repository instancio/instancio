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

import org.instancio.FieldSelectorBuilder;
import org.instancio.GroupableSelector;
import org.instancio.PredicateSelector;
import org.instancio.SelectorGroup;
import org.instancio.TargetSelector;
import org.instancio.TypeSelectorBuilder;
import org.instancio.internal.Flattener;
import org.instancio.internal.util.Fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.joining;

public final class SelectorGroupImpl implements SelectorGroup, Flattener<TargetSelector> {

    private final List<GroupableSelector> selectors;
    private final List<TargetSelector> flattened = new ArrayList<>();

    public SelectorGroupImpl(final GroupableSelector... selectors) {
        this.selectors = Collections.unmodifiableList(Arrays.asList(selectors));

        for (GroupableSelector selector : selectors) {
            if (selector instanceof SelectorImpl
                    || selector instanceof FieldSelectorBuilder
                    || selector instanceof TypeSelectorBuilder
                    || selector instanceof PredicateSelector) {
                flattened.add(selector);
            } else if (selector instanceof PrimitiveAndWrapperSelectorImpl) {
                flattened.addAll(((PrimitiveAndWrapperSelectorImpl) selector).flatten());
            } else {
                throw Fail.withFataInternalError("Unhandled selector: " + selector.getClass());
            }
        }
    }

    public List<GroupableSelector> getSelectors() {
        return selectors;
    }

    @Override
    public List<TargetSelector> flatten() {
        return flattened;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof SelectorGroupImpl)) return false;
        final SelectorGroupImpl that = (SelectorGroupImpl) o;
        return selectors.equals(that.selectors);
    }

    @Override
    public int hashCode() {
        return selectors.hashCode();
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
