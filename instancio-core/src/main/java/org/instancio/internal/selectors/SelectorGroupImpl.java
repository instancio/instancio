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

import org.instancio.GroupableSelector;
import org.instancio.SelectorGroup;
import org.instancio.TargetSelector;
import org.instancio.internal.Flattener;
import org.instancio.internal.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

public final class SelectorGroupImpl implements SelectorGroup, Flattener<TargetSelector> {

    private final List<GroupableSelector> selectors;
    private final List<TargetSelector> flattened = new ArrayList<>();

    public SelectorGroupImpl(final GroupableSelector... selectors) {
        this.selectors = CollectionUtils.asUnmodifiableList(selectors);

        for (GroupableSelector selector : selectors) {
            flattened.addAll(((Flattener<TargetSelector>) selector).flatten());
        }
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
