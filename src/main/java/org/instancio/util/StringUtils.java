package org.instancio.util;

public final class StringUtils {

    private StringUtils() {
    }

    public static String repeat(String s, int times) {
        if (times < 0) throw new IllegalArgumentException("Number of times must be positive: " + times);
        StringBuilder sb = new StringBuilder(s.length() * times);
        while (times-- > 0) sb.append(s);
        return sb.toString();
    }
}
