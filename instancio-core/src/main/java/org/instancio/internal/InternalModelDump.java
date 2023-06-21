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

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeStats;
import org.instancio.internal.util.Format;

import java.util.function.Consumer;

import static org.instancio.internal.util.Constants.NL;

final class InternalModelDump {

    private final Consumer<String> consumer;

    InternalModelDump(final Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @SuppressWarnings("PMD.SystemPrintln")
    static void printVerbose(final InternalModel<?> model) {
        InternalModelDump modelDump = new InternalModelDump(System.out::println); // NOSONAR
        modelDump.consume(model);
    }

    void consume(final InternalModel<?> model) {
        if (model.getModelContext().isVerbose()) {
            final String modelDump = createModelDump(model);
            consumer.accept(modelDump);
        }
    }

    private static String createModelDump(final InternalModel<?> model) {
        final ModelContext<?> context = model.getModelContext();
        final InternalNode rootNode = model.getRootNode();
        final NodeStats nodeStats = NodeStats.compute(rootNode);
        final String location = Format.firstNonInstancioStackTraceLine(new Throwable());

        //noinspection StringBufferReplaceableByString
        return new StringBuilder(10_000)
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
                .append("### Node hierarchy").append(NL)
                .append(NL)
                .append("Format: <depth:class: field>").append(NL)
                .append(NL)
                .append(nodeStats.getTreeString()).append(NL)
                .append(" -> Node max depth ........: ").append(nodeStats.getHeight()).append(NL)
                .append(" -> Model max depth .......: ").append(context.getMaxDepth()).append(NL)
                .append(" -> Total nodes ...........: ").append(nodeStats.getTotalNodes()).append(NL)
                .append(" -> Seed ..................: ").append(context.getRandom().getSeed()).append(NL)
                .append(NL)
                .append("Done. The create() method that was run with verbose() enabled at:").append(NL)
                .append(location).append(NL)
                .append("________________________________________________________________________________________")
                .toString();
    }
}
