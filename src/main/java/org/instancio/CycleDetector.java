package org.instancio;

import org.instancio.model.Node;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;

class CycleDetector {

    static class InstanceNode {
        private final Object instance;
        private final Node node;

        InstanceNode(@Nullable final Object instance, final Node node) {
            this.instance = instance;
            this.node = node;
        }
    }

    /**
     * Maps object instance to its ancestor instance
     */
    private final Map<Object, InstanceNode> idMap = new IdentityHashMap<>();

    void setAncestorOf(final Object instance, final InstanceNode ancestor) {
        idMap.put(instance, ancestor);
    }

    Object getAncestorWithClass(@Nullable final Object instance, final Node node, final Class<?> ancestorClass) {
        InstanceNode ancestor = idMap.get(instance);

        while (ancestor != null) {
            if (ancestor.instance != null
                    && ancestor.instance.getClass() == ancestorClass
                    && node.equals(ancestor.node)) { // XXX check for same Class and Node?

                return ancestor;
            }
            ancestor = idMap.get(ancestor);
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hierarchy {\n");
        idMap.forEach((k, v) -> sb
                .append("  [").append(System.identityHashCode(k)).append("] ")
                .append(k.getClass().getSimpleName())
                .append(" -> ")
                .append(v == null ? "null" : v.getClass().getSimpleName())
                .append(" [").append(System.identityHashCode(v)).append("]\n"));
        return sb.append("}").toString();
    }
}
