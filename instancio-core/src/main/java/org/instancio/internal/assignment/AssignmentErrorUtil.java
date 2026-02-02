/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.internal.assignment;

import org.instancio.TargetSelector;
import org.instancio.internal.DelayedNode;
import org.instancio.internal.DelayedNodeQueue;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Format;

import java.util.Set;

import static org.instancio.internal.util.Constants.NL;

public final class AssignmentErrorUtil {

    private AssignmentErrorUtil() {
        // non-instantiable
    }

    public static String getUnresolvedAssignmentErrorMessage(
            final Set<InternalAssignment> unresolvedAssignments,
            final DelayedNodeQueue delayedNodeQueue) {

        final StringBuilder sb = new StringBuilder(2048)
                .append("unresolved assignment expression").append(NL)
                .append(NL)
                .append("The following assignments could not be applied:").append(NL)
                .append(NL);

        for (InternalAssignment assignment : unresolvedAssignments) {
            sb.append(" -> from [")
                    .append(assignment.getOrigin())
                    .append("] to [")
                    .append(assignment.getDestination())
                    .append(']')
                    .append(NL);
        }

        sb.append(NL)
                .append("As a result, the following targets could not be assigned a value:").append(NL)
                .append(NL);

        // Avoid duplicate report entries if the same InternalNode instance is delayed multiple times.
        delayedNodeQueue.stream()
                .map(DelayedNode::getNode)
                .distinct()
                .forEach(node -> sb.append(" -> ").append(node.toDisplayString())
                        .append(" (depth=").append(node.getDepth()).append(')')
                        .append(NL));

        sb.append(NL)
                .append("Possible causes:").append(NL)
                .append(NL)
                .append(" -> The assignments form a cycle, for example:").append(NL)
                .append(NL)
                .append("    Pojo pojo = Instancio.of(Pojo.class)").append(NL)
                .append("        .assign(Assign.valueOf(Pojo::getFoo).to(Pojo::getBar))").append(NL)
                .append("        .assign(Assign.valueOf(Pojo::getBar).to(Pojo::getFoo))").append(NL)
                .append("        .create();").append(NL)
                .append(NL)
                .append(" -> Part of the assignment expression is ignored using the ignore() method:").append(NL)
                .append(NL)
                .append("    Person person = Instancio.of(Person.class)").append(NL)
                .append("        .ignore(field(Person::getGender)) // ignored!").append(NL)
                .append("        .assign(Assign.given(field(Person::getGender), field(Person::getName))").append(NL)
                .append("                .set(When.is(Gender.FEMALE), \"Fiona\")").append(NL)
                .append("                .set(When.is(Gender.MALE), \"Michael\"))").append(NL)
                .append("        .create();").append(NL);

        return sb.toString();
    }

    @SuppressWarnings({"StringBufferReplaceableByString", "UnnecessaryStringBuilder"})
    public static String getAmbiguousErrorMessage(
            final TargetSelector selector,
            final InternalNode matchingNode1,
            final InternalNode matchingNode2) {

        final StringBuilder sb = new StringBuilder(2048)
                .append("ambiguous assignment expression").append(NL)
                .append(NL)
                .append(" -> The origin selector '").append(selector).append("' matches multiple values.").append(NL)
                .append("    It's not clear which of these values should be used:").append(NL)
                .append(NL)
                .append(" -> Match 1: ").append(matchingNode1.toDisplayString()).append(NL)
                .append(NL)
                .append(Format.nodePathToRoot(matchingNode1, "    ")).append(NL)
                .append(NL)
                .append(" -> Match 2: ").append(matchingNode2.toDisplayString()).append(NL)
                .append(NL)
                .append(Format.nodePathToRoot(matchingNode2, "    ")).append(NL)
                .append(NL)
                .append("Format: <depth:class: field>").append(NL)
                .append(NL)
                .append("There could be more matches. Evaluation stopped after the second match.").append(NL)
                .append("To print the node hierarchy, run Instancio in verbose() mode:").append(NL)
                .append(NL)
                .append("  Instancio.of(Example.class)").append(NL)
                .append("      // snip ...").append(NL)
                .append("      .verbose()").append(NL)
                .append("      .create();").append(NL)
                .append(NL)
                .append("To resolve the error, consider narrowing down the origin selector").append(NL)
                .append("so that it matches only one target. This can be done using:").append(NL)
                .append(NL)
                .append(" -> Scopes").append(NL)
                .append("    https://www.instancio.org/user-guide/#selector-scopes").append(NL)
                .append(NL)
                .append(" -> Depth").append(NL)
                .append("    https://www.instancio.org/user-guide/#selector-depth");

        return sb.toString();
    }
}
