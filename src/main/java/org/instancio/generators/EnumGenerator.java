package org.instancio.generators;

import org.instancio.exception.InstancioException;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

import java.lang.reflect.Method;

public class EnumGenerator extends AbstractGenerator<Enum<?>> {
    private final Class<?> enumClass;

    public EnumGenerator(final RandomProvider random, final Class<?> enumClass) {
        super(random);
        this.enumClass = Verify.notNull(enumClass, "Enum class must not be null");
    }

    @Override
    public Enum<?> generate() {
        try {
            Method m = enumClass.getDeclaredMethod("values");
            Enum<?>[] res = (Enum<?>[]) m.invoke(null);
            return random().from(res);
        } catch (Exception ex) {
            throw new InstancioException("Error generating enum value for: " + enumClass.getName(), ex);
        }
    }
}
