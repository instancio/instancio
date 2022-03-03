package org.instancio;

import org.instancio.exception.InstancioException;
import org.instancio.generator.ValueGenerator;

import java.util.Arrays;

public class ObjectCreationSettingsAPI<C, T extends CreationSettingsAPI<C, T>>
        extends AbstractCreationSettingsAPI<C, T> {

    ObjectCreationSettingsAPI(Class<C> klass) {
        super(klass);
    }

    @Override
    public ObjectCreationSettingsAPI<C, T> exclude(String... fields) {
        super.exclude(fields);
        return this;
    }

    @Override
    public <V> ObjectCreationSettingsAPI<C, T> with(String field, ValueGenerator<V> generator) {
        super.with(field, generator);
        return this;
    }

    @Override
    public <V> ObjectCreationSettingsAPI<C, T> with(Class<V> klass, ValueGenerator<V> generator) {
        super.with(klass, generator);
        return this;
    }

    @Override
    public ObjectCreationSettingsAPI<C, T> withNullable(String field) {
        super.withNullable(field);
        return this;
    }

    @Override
    public ObjectCreationSettingsAPI<C, T> withType(Class<?>... types) {
        super.withType(types);
        return this;
    }

    public C create() {
        // TODO klass if passed in context... remove from create method arg?


        // check if klass is generic, check if number of genericTypes specified matches the number of TypeVariables
        if (klass.getTypeParameters().length != genericTypes.size()) {
            // TODO improve error message with actual arguments included since we know the types...
            throw new InstancioException("Generic class " + klass.getName() + " has " + klass.getTypeParameters().length
                                         + " type parameters: " + Arrays.toString(klass.getTypeParameters())
                                         + ". Please specify all type parameters using 'withType(Class... types)`");
        }

        InstancioContext context = new InstancioContext(
                klass, exclusions, nullables, fieldValueGenerators, classValueGenerators, genericTypes);
        InstancioDriver driver = new InstancioDriver(context);
        return driver.create(klass);
    }
}
