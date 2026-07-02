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
package org.instancio.internal.selectors;

import org.instancio.Scope;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeKind;
import org.instancio.internal.util.Fail;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public record ElementOfDescriptor(
        Predicate<InternalNode> containerPredicate,
        List<Scope> containerScopes,
        @Nullable ElementIndexFilter elementIndexFilter,
        @Nullable Predicate<InternalNode> innerNodePredicate,
        boolean containerWasRoot,
        boolean requireIndexedContainer,
        // true when baked from a user's elementOf() call; false for a setModel rebakedCopy()
        boolean isUserOriginated,
        String descriptionSuffix) {

    public int requiredMinSize() {
        return elementIndexFilter == null ? 0 : elementIndexFilter.requiredMinSize();
    }

    public ElementOfDescriptor rebakedCopy(
            final Predicate<InternalNode> containerPredicate,
            final @Nullable Predicate<InternalNode> innerNodePredicate) {

        return new ElementOfDescriptor(
                containerPredicate, containerScopes, elementIndexFilter,
                innerNodePredicate, false, false, /* isUserOriginated */ false,
                descriptionSuffix);
    }

    public boolean matchesContainer(final InternalNode containerNode) {
        return containerPredicate.test(containerNode)
                && SelectorScopeMatcher.matches(containerScopes, containerNode);
    }

    public String describe(final String containerDescription) {
        return "elementOf(" + containerDescription + ')' + descriptionSuffix;
    }

    public boolean matches(
            final InternalNode node,
            final ElementFrameStack.@Nullable Frame frame) {

        if (frame == null) {
            return false;
        }
        final InternalNode containerNode = frame.containerNode();
        if (!matchesContainer(containerNode)) {
            return false;
        }

        if (requireIndexedContainer && !isIndexedContainerNode(containerNode)) {
            // Should not be reachable with existing validation
            throw Fail.withFataInternalError(
                    "Indexed container expected, but got: %s", containerNode);
        }

        if (elementIndexFilter != null
                && !elementIndexFilter.contains(frame.index(), frame.containerSize())) {
            return false;
        }
        return innerNodePredicate == null
                ? node.getParent() == containerNode //NOPMD reference equality intended
                : innerNodePredicate.test(node);
    }

    public boolean matchesStatically(final InternalNode node) {
        if (innerNodePredicate != null && !innerNodePredicate.test(node)) {
            return false;
        }
        final InternalNode container = innerNodePredicate == null
                ? node.getParent()
                : nearestFramePushingContainer(node);

        return container != null
                && isFramePushingContainer(container)
                && matchesContainer(container);
    }

    private static boolean isIndexedContainerNode(final InternalNode containerNode) {
        return containerNode.is(NodeKind.ARRAY)
                || List.class.isAssignableFrom(containerNode.getTargetClass());
    }

    @Nullable
    private static InternalNode nearestFramePushingContainer(final InternalNode node) {
        InternalNode ancestor = node.getParent();
        while (ancestor != null && !isFramePushingContainer(ancestor)) {
            ancestor = ancestor.getParent();
        }
        return ancestor;
    }

    private static boolean isFramePushingContainer(final InternalNode node) {
        return node.is(NodeKind.COLLECTION) || node.is(NodeKind.ARRAY);
    }
}
