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
import org.instancio.internal.util.Sonar;

import java.util.Set;

import static java.util.stream.Collectors.joining;
import static org.instancio.internal.util.Constants.NL;

final class UnusedSelectorReporter {
    private final int maxDepth;
    private final Set<TargetSelector> ignored;
    private final Set<TargetSelector> nullable;
    private final Set<TargetSelector> generators;
    private final Set<TargetSelector> callbacks;
    private final Set<TargetSelector> subtypes;
    private final Set<TargetSelector> assignmentOrigins;
    private final Set<TargetSelector> assignmentDestinations;


    private UnusedSelectorReporter(final Builder builder) {
        maxDepth = builder.maxDepth;
        ignored = builder.ignored;
        nullable = builder.nullable;
        generators = builder.generators;
        callbacks = builder.callbacks;
        subtypes = builder.subtypes;
        assignmentOrigins = builder.assignmentOrigins;
        assignmentDestinations = builder.assignmentDestinations;
    }

    static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings(Sonar.STRING_LITERALS_DUPLICATED)
    void report() {
        if (hasNoUnusedSelectors()) {
            return;
        }

        final StringBuilder sb = new StringBuilder(2048)
                .append(NL)
                .append("Found unused selectors referenced in the following methods:")
                .append(NL);

        append(ignored, sb, "selectors", "ignore()");
        append(nullable, sb, "selectors", "withNullable()");
        append(generators, sb, "selectors", "generate(), set(), or supply()");
        append(callbacks, sb, "selectors", "onComplete()");
        append(subtypes, sb, "selectors", "subtype()");
        append(assignmentOrigins, sb, "origin selectors", "assign()");
        append(assignmentDestinations, sb, "destination selectors", "assign()");

        sb.append(NL)
                .append("This error aims to highlight potential problems and help maintain clean test code.").append(NL)
                .append(NL)
                .append("Possible causes:").append(NL)
                .append(NL)
                .append(" -> Selector did not match any field or class within this object.").append(NL)
                .append(" -> Selector target is beyond the current maximum depth setting: ").append(maxDepth).append(NL)
                .append(" -> Selector matches an ignored target, for example:").append(NL)
                .append(NL)
                .append("    Person person = Instancio.of(Person.class)").append(NL)
                .append("        .ignore(all(Phone.class))").append(NL)
                .append("        .set(field(Phone::getNumber), \"555-66-77\") // unused!").append(NL)
                .append("        .create();").append(NL)
                .append(NL)
                .append(" -> Selector targets a field or class in an object that was provided by:").append(NL)
                .append("    -> set(TargetSelector, Object)").append(NL)
                .append("    -> supply(TargetSelector, Supplier)").append(NL)
                .append(NL)
                .append("    // Example").append(NL)
                .append("    Supplier<Address> addressSupplier = () -> new Address(...);").append(NL)
                .append("    Person person = Instancio.of(Person.class)").append(NL)
                .append("        .supply(all(Address.class), () -> addressSupplier)").append(NL)
                .append("        .set(field(Address::getCity), \"London\") // unused!").append(NL)
                .append("        .create();").append(NL)
                .append(NL)
                .append("    Instancio does not modify instances provided by a Supplier,").append(NL)
                .append("    therefore, field(Address::getCity) will trigger unused selector error.").append(NL)
                .append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> Remove the selector(s) causing the error, if applicable.").append(NL)
                .append(" -> Suppress the error by enabling 'lenient()' mode:").append(NL)
                .append(NL)
                .append("    Example example = Instancio.of(Example.class)").append(NL)
                .append("        .lenient()").append(NL)
                .append("        .create();").append(NL)
                .append(NL)
                .append("    // Or via Settings").append(NL)
                .append("    Settings settings = Settings.create()").append(NL)
                .append("        .set(Keys.MODE, Mode.LENIENT);").append(NL)
                .append(NL)
                .append("    Example example = Instancio.of(Example.class)").append(NL)
                .append("        .withSettings(settings)").append(NL)
                .append("        .create();").append(NL)
                .append(NL)
                .append("For more information see: https://www.instancio.org/user-guide/#selector-strictness")
                .append(NL);

        throw new UnusedSelectorException(sb.toString(), ignored, nullable, generators, callbacks, subtypes);
    }

    private static void append(
            final Set<TargetSelector> selectors,
            final StringBuilder sb,
            final String selectorDescription,
            final String apiMethodName) {

        if (!selectors.isEmpty()) {
            sb.append(NL)
                    .append(" -> Unused ")
                    .append(selectorDescription)
                    .append(" in ")
                    .append(apiMethodName)
                    .append(':')
                    .append(NL)
                    .append(formatSelectors(selectors))
                    .append(NL);
        }
    }

    private boolean hasNoUnusedSelectors() {
        return ignored.isEmpty()
                && nullable.isEmpty()
                && generators.isEmpty()
                && callbacks.isEmpty()
                && subtypes.isEmpty()
                && assignmentOrigins.isEmpty()
                && assignmentDestinations.isEmpty();
    }

    private static String formatSelectors(final Set<TargetSelector> selectors) {
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
        private int maxDepth;
        private Set<TargetSelector> ignored;
        private Set<TargetSelector> nullable;
        private Set<TargetSelector> generators;
        private Set<TargetSelector> callbacks;
        private Set<TargetSelector> subtypes;
        private Set<TargetSelector> assignmentOrigins;
        private Set<TargetSelector> assignmentDestinations;

        private Builder() {
        }

        public Builder maxDepth(final int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        public Builder ignored(final Set<TargetSelector> ignored) {
            this.ignored = ignored;
            return this;
        }

        public Builder nullable(final Set<TargetSelector> nullable) {
            this.nullable = nullable;
            return this;
        }

        public Builder generators(final Set<TargetSelector> generators) {
            this.generators = generators;
            return this;
        }

        public Builder callbacks(final Set<TargetSelector> callbacks) {
            this.callbacks = callbacks;
            return this;
        }

        public Builder subtypes(final Set<TargetSelector> subtypes) {
            this.subtypes = subtypes;
            return this;
        }

        public Builder assignmentOrigins(final Set<TargetSelector> assignmentOrigins) {
            this.assignmentOrigins = assignmentOrigins;
            return this;
        }

        public Builder assignmentDestinations(final Set<TargetSelector> assignmentDestinations) {
            this.assignmentDestinations = assignmentDestinations;
            return this;
        }

        public UnusedSelectorReporter build() {
            return new UnusedSelectorReporter(this);
        }
    }
}
