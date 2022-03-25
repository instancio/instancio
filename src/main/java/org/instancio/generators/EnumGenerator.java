package org.instancio.generators;

import org.instancio.exception.InstancioException;
import org.instancio.internal.model.ModelContext;
import org.instancio.util.Verify;

import java.lang.reflect.Method;

public class EnumGenerator extends AbstractRandomGenerator<Enum<?>> {
    private final Class<?> enumClass;

    public EnumGenerator(final ModelContext<?> context, final Class<?> enumClass) {
        super(context);
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
