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
package org.instancio.internal;

import org.instancio.Random;
import org.instancio.TargetSelector;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeStats;
import org.instancio.internal.selectors.InternalSelector;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.StringUtils;
import org.instancio.support.Seeds;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.instancio.internal.util.Constants.NL;

final class ModelReporter {

    private final Consumer<String> consumer;

    private ModelReporter(final Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @SuppressWarnings("PMD.SystemPrintln")
    static void report(final InternalModel<?> model) {
        final Random random = model.getModelContext().getRandom();
        Seeds.logSeed(random, model.getRootNode().getType());
        printVerbose(model);
    }

    @SuppressWarnings("PMD.SystemPrintln")
    static void printVerbose(final InternalModel<?> model) {
        ModelReporter modelDump = new ModelReporter(System.out::println); // NOSONAR
        modelDump.consume(model);
    }

    void consume(final InternalModel<?> model) {
        if (model.getModelContext().isVerbose()) {
            final String modelDump = createModelDump(model);
            consumer.accept(modelDump);
        }
    }

    private static String createModelDump(final InternalModel<?> model) {
        final ModelContext context = model.getModelContext();
        final InternalNode rootNode = model.getRootNode();
        final NodeStats nodeStats = NodeStats.compute(rootNode);
        final String location = Format.firstNonInstancioStackTraceLine(new Throwable());

        final StringBuilder sb = new StringBuilder(10_000)
                .append(" _____              _                       _          __  __             _        _").append(NL)
                .append("|_   _|            | |                     (_)        |  \\/  |           | |      | |").append(NL)
                .append("  | |   _ __   ___ | |_  __ _  _ __    ___  _   ___   | \\  / |  ___    __| |  ___ | |").append(NL)
                .append("  | |  | '_ \\ / __|| __|/ _` || '_ \\  / __|| | / _ \\  | |\\/| | / _ \\  / _` | / _ \\| |").append(NL)
                .append(" _| |_ | | | |\\__ \\| |_| (_| || | | || (__ | || (_) | | |  | || (_) || (_| ||  __/| |").append(NL)
                .append("|_____||_| |_||___/ \\__|\\__,_||_| |_| \\___||_| \\___/  |_|  |_| \\___/  \\__,_| \\___||_|").append(NL)
                .append("________________________________________________________________________________________").append(NL)
                .append(NL)
                .append(" -> Instancio model for ").append(Format.withoutPackage(rootNode.getType())).append(NL)
                .append("    at ").append(location).append(NL)
                .append(NL)
                .append("### Settings").append(NL)
                .append(NL)
                .append(context.getSettings()).append(NL)
                .append(NL)
                .append("### Nodes").append(NL)
                .append(NL)
                .append("Format: <depth:class: field>").append(NL)
                .append(NL)
                .append(nodeStats.getTreeString()).append(NL)
                .append(" -> Node max depth ........: ").append(nodeStats.getHeight()).append(NL)
                .append(" -> Model max depth .......: ").append(context.getMaxDepth()).append(NL)
                .append(" -> Total nodes ...........: ").append(nodeStats.getTotalNodes()).append(NL)
                .append(" -> Seed ..................: ").append(context.getRandom().getSeed()).append(NL)
                .append(NL);

        appendSelectorMatches(sb, model);

        return sb
                .append("________________________________________________________________________________________").append(NL)
                .append("Done. Reminder to remove verbose() from:").append(NL)
                .append(location).append(NL)
                .toString();
    }

    private static void appendSelectorMatches(final StringBuilder sb, final InternalModel<?> model) {
        final InternalNode rootNode = model.getRootNode();
        final ModelContext context = model.getModelContext();
        final Map<ApiMethodSelector, Map<TargetSelector, Set<InternalNode>>> map = context.getSelectors(rootNode);

        sb.append("### Selectors").append(NL)
                .append(NL)
                .append("Selectors and matching nodes, if any:").append(NL)
                .append(NL);

        for (Map.Entry<ApiMethodSelector, Map<TargetSelector, Set<InternalNode>>> entry : map.entrySet()) {
            final Map<TargetSelector, Set<InternalNode>> selectorNodes = entry.getValue();

            if (selectorNodes.isEmpty()) {
                continue;
            }

            final String section = selectorNodes.entrySet().stream()
                    .map(e -> {
                        final InternalSelector selector = (InternalSelector) e.getKey();

                        if (!selector.isHiddenFromVerboseOutput()) {
                            final StringBuilder tmp = new StringBuilder();
                            tmp.append("    - ").append(selector).append(NL);

                            final Set<InternalNode> nodes = e.getValue();
                            nodes.forEach(node -> tmp.append("       \\_ ").append(node).append(NL));
                            tmp.append(NL);
                            return tmp.toString();
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(""));

            if (!StringUtils.isBlank(section)) {
                final ApiMethodSelector method = entry.getKey();
                sb.append(" -> Method: ").append(method.getDescription()).append(NL)
                        .append(section);
            }
        }
    }
}
