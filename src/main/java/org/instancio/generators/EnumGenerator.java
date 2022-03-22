package org.instancio.generators;

import org.instancio.Generator;
import org.instancio.exception.InstancioException;
import org.instancio.util.Random;
import org.instancio.util.Verify;

import java.lang.reflect.Method;

public class EnumGenerator implements Generator<Enum<?>> {
    private final Class<?> enumClass;

    public EnumGenerator(Class<?> enumClass) {
        this.enumClass = Verify.notNull(enumClass, "Enum class must not be null");
    }

    @Override
    public Enum<?> generate() {
        try {
            Method m = enumClass.getDeclaredMethod("values");
            Enum<?>[] res = (Enum<?>[]) m.invoke(null);
            return res[Random.intBetween(0, res.length)];
        } catch (Exception ex) {
            throw new InstancioException("Error generating enum value for: " + enumClass.getName(), ex);
        }
    }
}
