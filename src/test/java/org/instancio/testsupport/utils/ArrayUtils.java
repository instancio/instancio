package org.instancio.testsupport.utils;

import java.util.ArrayList;
import java.util.List;

public final class ArrayUtils {
    private ArrayUtils() {
        // non-instantiable
    }

    public static List<Integer> toList(int... values) {
        List<Integer> result = new ArrayList<>();
        for (int n : values) result.add(n);
        return result;
    }

}
