package org.instancio.testsupport.utils;

import java.util.Collection;

public class CollectionUtils {

    private CollectionUtils() {
        // non-instantiable
    }

    public static <T> T getOnlyElement(Collection<T> collection) {
        if (collection.size() != 1) {
            throw new AssertionError("Collection expected to have exactly one element but has: " + collection.size());
        }
        return collection.iterator().next();
    }

}
