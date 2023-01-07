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
package org.instancio.internal.context;

import org.instancio.TargetSelector;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.internal.selectors.UnusedSelectorDescription;

import java.util.Set;

import static java.util.stream.Collectors.joining;
import static org.instancio.internal.util.Constants.NL;

final class UnusedSelectorReporter {
    private final Set<? super TargetSelector> ignored;
    private final Set<? super TargetSelector> nullable;
    private final Set<? super TargetSelector> generators;
    private final Set<? super TargetSelector> callbacks;
    private final Set<? super TargetSelector> subtypes;

    private UnusedSelectorReporter(final Builder builder) {
        ignored = builder.ignored;
        nullable = builder.nullable;
        generators = builder.generators;
        callbacks = builder.callbacks;
        subtypes = builder.subtypes;
    }

    public static Builder builder() {
        return new Builder();
    }

    void report() {
        if (hasNoUnusedSelectors()) {
            return;
        }

        final StringBuilder sb = new StringBuilder(1024)
                .append(NL)
                .append("Found unused selectors referenced in the following methods:")
                .append(NL);

        append(ignored, sb, "ignore()");
        append(nullable, sb, "withNullable()");
        append(generators, sb, "generate(), set(), or supply()");
        append(callbacks, sb, "onComplete()");
        append(subtypes, sb, "subtype()");

        sb.append(NL)
                .append("This error aims to highlight potential problems and help maintain clean test code.").append(NL)
                .append("You are most likely selecting a field or class that does not exist within this object.").append(NL).append(NL)
                .append("This error can be suppressed by switching to lenient mode, for example:").append(NL).append(NL)
                .append("      Example example = Instancio.of(Example.class)").append(NL)
                .append("          // snip...").append(NL)
                .append("          .lenient().create();").append(NL).append(NL)
                .append("For more information see: https://www.instancio.org/user-guide/").append(NL);

        throw new UnusedSelectorException(sb.toString());
    }

    private static void append(
            final Set<? super TargetSelector> selectors,
            final StringBuilder sb,
            final String apiMethodName) {

        if (!selectors.isEmpty()) {
            sb.append(NL)
                    .append(" -> Unused selectors in ").append(apiMethodName).append(':')
                    .append(NL)
                    .append(formatSelectors(selectors))
                    .append(NL);
        }
    }

    private boolean hasNoUnusedSelectors() {
        return ignored.isEmpty() && nullable.isEmpty() && generators.isEmpty() && callbacks.isEmpty() && subtypes.isEmpty();
    }

    private static String formatSelectors(final Set<? super TargetSelector> selectors) {
        final int[] count = {1};
        return selectors.stream()
                .map(UnusedSelectorDescription.class::cast)
                .map(UnusedSelectorDescription::getDescription)
                // when selectors are flattened, PrimitiveAndWrapperSelector results in two entries,
                // one for primitive and one for wrapper. Using distinct to prevent duplicate entries.
                .distinct()
                .sorted()
                .map(it -> String.format(" %s: %s", count[0]++, it))
                .collect(joining(NL));
    }

    public static final class Builder {
        private Set<? super TargetSelector> ignored;
        private Set<? super TargetSelector> nullable;
        private Set<? super TargetSelector> generators;
        private Set<? super TargetSelector> callbacks;
        private Set<? super TargetSelector> subtypes;

        private Builder() {
        }

        public Builder ignored(final Set<? super TargetSelector> ignored) {
            this.ignored = ignored;
            return this;
        }

        public Builder nullable(final Set<? super TargetSelector> nullable) {
            this.nullable = nullable;
            return this;
        }

        public Builder generators(final Set<? super TargetSelector> generators) {
            this.generators = generators;
            return this;
        }

        public Builder callbacks(final Set<? super TargetSelector> callbacks) {
            this.callbacks = callbacks;
            return this;
        }

        public Builder subtypes(final Set<? super TargetSelector> subtypes) {
            this.subtypes = subtypes;
            return this;
        }

        public UnusedSelectorReporter build() {
            return new UnusedSelectorReporter(this);
        }
    }
}
