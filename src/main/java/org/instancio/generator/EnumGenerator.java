package org.instancio.generator;

import org.instancio.util.Random;

import java.lang.reflect.Method;

public class EnumGenerator implements Generator<Enum<?>> {
    private final Class<?> enumClass;

    public EnumGenerator(Class<?> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public Enum<?> generate() {
        try {
            Method m = enumClass.getDeclaredMethod("values");
            Enum<?>[] res = (Enum<?>[]) m.invoke(null);
            return res[Random.intBetween(0, res.length)];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // TODO
    }
}
