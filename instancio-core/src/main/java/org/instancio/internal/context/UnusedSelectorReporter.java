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

import org.instancio.TargetSelector;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.internal.ApiMethodSelector;
import org.instancio.internal.selectors.InternalSelector;
import org.instancio.internal.selectors.UnusedSelectorDescription;
import org.instancio.internal.util.Sonar;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static org.instancio.internal.util.Constants.NL;

final class UnusedSelectorReporter {
    private final int maxDepth;
    private final SelectorNodeMatchesCollector collector;

    UnusedSelectorReporter(final int maxDepth, final SelectorMaps selectorMaps) {
        this.maxDepth = maxDepth;
        this.collector = new SelectorNodeMatchesCollector(selectorMaps);
    }

    @SuppressWarnings(Sonar.STRING_LITERALS_DUPLICATED)
    void report() {
        final List<TargetSelector> unusedSelectors = collector.getUnusedSelectors();
        if (unusedSelectors.isEmpty()) {
            return;
        }

        final Map<ApiMethodSelector, List<TargetSelector>> unused = unusedSelectors.stream()
                .collect(Collectors.groupingBy((TargetSelector s) -> {
                    final InternalSelector selector = (InternalSelector) s;
                    return selector.getApiMethodSelector();
                }));

        final StringBuilder sb = new StringBuilder(2048)
                .append(NL)
                .append("Found unused selectors referenced in the following methods:")
                .append(NL);

        for (ApiMethodSelector apiMethodSelector : ApiMethodSelector.values()) {
            if (apiMethodSelector != ApiMethodSelector.NONE) {
                append(unused, sb, apiMethodSelector);
            }
        }

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

        throw new UnusedSelectorException(sb.toString(), unused);
    }

    private static void append(
            final Map<ApiMethodSelector, List<TargetSelector>> map,
            final StringBuilder sb,
            final ApiMethodSelector apiMethod) {

        final List<TargetSelector> selectors = map.get(apiMethod);
        if (selectors != null) {
            sb.append(NL)
                    .append(" -> Unused selector in: ")
                    .append(apiMethod.getDescription())
                    .append(NL)
                    .append(formatSelectors(selectors))
                    .append(NL);
        }
    }

    private static String formatSelectors(final List<TargetSelector> selectors) {
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
}
