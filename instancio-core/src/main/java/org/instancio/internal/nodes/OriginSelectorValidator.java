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
package org.instancio.internal.nodes;

import org.instancio.TargetSelector;
import org.instancio.internal.util.Fail;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.instancio.internal.util.Constants.NL;

/**
 * Helper class for checking that an origin selector does not match more
 * than one node. If an origin matches more than one node, then it is
 * not clear which node's value should be satisfied by the origin predicate.
 * This leads to unpredictable results.
 *
 * <p>Note: this check is performed on a best-effort basis.
 * It is <b>not</b> guaranteed to cover all possible ambiguous cases.
 * For example, if a node is part of a collection. Assuming we are
 * given a {@code List<Person>} and an origin {@code Person::age},
 * the conditional may or may not be ambiguous depending on whether
 * the destination selector is within the list (not ambiguous)
 * or outside the list (ambiguous). This is because of conditional
 * scoping performed by the engine.
 *
 * <pre>{@code
 *
 *   // Not ambiguous: scoping rules applied to each collection element
 *   when(valueOf(Person::age)
 *       .satisfies(age -> age > 18)
 *       .set(field(Person::type), ADULT))}
 *
 *   // Ambiguous: which list element should satisfy the condition?
 *   // However, no error will be generated in this case - user beware!
 *   when(valueOf(Person::age)
 *       .satisfies(age -> age > 18)
 *       .set(field(SomePojoOutsideTheList::someField), "some-value"))}
 * }</pre>
 */
class OriginSelectorValidator {

    private final NodeContext nodeContext;
    private final Map<TargetSelector, InternalNode> seen = new HashMap<>();

    OriginSelectorValidator(final NodeContext nodeContext) {
        this.nodeContext = nodeContext;
    }

    void checkNode(final InternalNode node) {
        final Set<TargetSelector> originSelectors = nodeContext.getConditionalOriginSelectors(node);

        for (TargetSelector selector : originSelectors) {
            final InternalNode prevSeen = seen.put(selector, node);

            if (prevSeen != null) {
                // TODO add selector location using UnusedSelectorDescription
                throw Fail.withUsageError(getErrorMessage(selector, prevSeen, node));
            }
        }
    }

    private static String getErrorMessage(final TargetSelector selector,
                                          final InternalNode matchingNode1,
                                          final InternalNode matchingNode2) {

        final StringBuilder sb = new StringBuilder(1024)
                .append("ambiguous conditional statement").append(NL)
                .append(NL)
                .append(" -> The origin selector 'valueOf(").append(selector).append(")' matches multiple values.").append(NL)
                .append("    It's not clear which value the condition should be evaluated against:").append(NL)
                .append(NL)
                .append("    -> Match 1: ").append(matchingNode1);

        appendAncestors(sb, matchingNode1);

        sb.append(NL)
                .append("    -> Match 2: ").append(matchingNode2);

        appendAncestors(sb, matchingNode2);

        sb.append(NL)
                .append("There could be more matches. Evaluation stopped after the second match.").append(NL)
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

    private static void appendAncestors(final StringBuilder sb, final InternalNode node) {
        if (node.getParent() == null) {
            return;
        }

        sb.append(NL).append("       Ancestors:").append(NL);

        InternalNode p = node.getParent();
        while (p != null) {
            sb.append("       -> ").append(p).append(NL);
            p = p.getParent();
        }
    }
}
