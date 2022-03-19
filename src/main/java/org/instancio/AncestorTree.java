package org.instancio;

import org.instancio.model.Node;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A class for detecting cyclic relationships among objects.
 * <p>
 * The idea is to use an {@link IdentityHashMap} to map a created object
 * to an {@link InstanceNode} containing the "parent" object and its node.
 * <p>
 * This allows to "walk up" the ancestors to check whether:
 * <ol>
 *   <li>the node that needs to created has occurred before, and if so</li>
 *   <li>whether the two nodes have the same parent</li>
 * </ol>
 * If both of these conditions are true, then there is a cycle.
 * <p>
 * This can be roughly illustrated as shown below, where {@code A} is the
 * current node to be created and {@code C} is its parent.
 * <pre>
 *     A -> B -> C -> A -> B -> C -> A
 *               ^^^^^^         ^^^^^^
 *                              cycle
 * </pre>
 */
class AncestorTree {

    /**
     * Maps object instance to its parent instance.
     */
    private final Map<Object, InstanceNode> idMap = new IdentityHashMap<>();

    void setObjectAncestor(final Object obj, final InstanceNode ancestor) {
        idMap.put(obj, ancestor);
    }

    Object getObjectAncestor(@Nullable final Object obj, final Node nodeToCreate) {
        InstanceNode ancestor = idMap.get(obj);

        while (ancestor != null) {
            if (ancestor.instance != null
                    && Objects.equals(nodeToCreate, ancestor.node)
                    && Objects.equals(nodeToCreate.getParent(), ancestor.node.getParent())) {
                return ancestor;
            }
            ancestor = idMap.get(ancestor.instance);
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(" {\n");
        idMap.forEach((k, v) -> sb
                .append("  [").append(System.identityHashCode(k)).append("] ")
                .append(k.getClass().getSimpleName())
                .append(" -> ")
                .append(v == null ? "null" : v.getClass().getSimpleName())
                .append(" [").append(System.identityHashCode(v)).append("]\n"));
        return sb.append("}").toString();
    }


    static class InstanceNode {
        private final Object instance;
        private final Node node;

        InstanceNode(@Nullable final Object instance, final Node node) {
            this.instance = instance;
            this.node = node;
        }
    }
}
