package org.instancio.generator;

import org.instancio.util.Random;

import java.lang.reflect.Method;

public class EnumGenerator implements ValueGenerator<Enum<?>> {
    private final Class<?> enumClass;

    public EnumGenerator(Class<?> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public Enum<?> generate() {
        try {
            Method m = enumClass.getDeclaredMethod("values");
            Enum<?>[] res = (Enum<?>[]) m.invoke(null);
            return res[Random.intBetween(0, res.length - 1)];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // TODO
    }
}
