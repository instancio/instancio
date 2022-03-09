package org.instancio.testsupport.utils;

import java.util.ArrayList;
import java.util.List;

public final class ArrayUtils {
    private ArrayUtils() {
        // non-instantiable
    }

    public static List<Byte> toList(byte... values) {
        List<Byte> result = new ArrayList<>();
        for (byte v : values) result.add(v);
        return result;
    }

    public static List<Short> toList(short... values) {
        List<Short> result = new ArrayList<>();
        for (short v : values) result.add(v);
        return result;
    }

    public static List<Integer> toList(int... values) {
        List<Integer> result = new ArrayList<>();
        for (int v : values) result.add(v);
        return result;
    }

    public static List<Long> toList(long... values) {
        List<Long> result = new ArrayList<>();
        for (long v : values) result.add(v);
        return result;
    }

    public static List<Float> toList(float... values) {
        List<Float> result = new ArrayList<>();
        for (float v : values) result.add(v);
        return result;
    }

    public static List<Double> toList(double... values) {
        List<Double> result = new ArrayList<>();
        for (double v : values) result.add(v);
        return result;
    }

    public static List<Boolean> toList(boolean... values) {
        List<Boolean> result = new ArrayList<>();
        for (boolean v : values) result.add(v);
        return result;
    }

    public static List<Character> toList(char... values) {
        List<Character> result = new ArrayList<>();
        for (char v : values) result.add(v);
        return result;
    }

}
