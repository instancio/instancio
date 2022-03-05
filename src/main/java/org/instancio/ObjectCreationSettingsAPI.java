package org.instancio;

import org.instancio.exception.InstancioException;
import org.instancio.generator.ValueGenerator;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ObjectCreationSettingsAPI<T, C extends CreationSettingsAPI<T, C>>
        extends AbstractCreationSettingsAPI<T, C> {

    ObjectCreationSettingsAPI(Class<T> klass) {
        super(klass);
    }

    @Override
    public ObjectCreationSettingsAPI<T, C> exclude(String... fields) {
        super.exclude(fields);
        return this;
    }

    @Override
    public <V> ObjectCreationSettingsAPI<T, C> with(String field, ValueGenerator<V> generator) {
        super.with(field, generator);
        return this;
    }

    @Override
    public <V> ObjectCreationSettingsAPI<T, C> with(Class<V> klass, ValueGenerator<V> generator) {
        super.with(klass, generator);
        return this;
    }

    @Override
    public ObjectCreationSettingsAPI<T, C> withNullable(String field) {
        super.withNullable(field);
        return this;
    }

    @Override
    public ObjectCreationSettingsAPI<T, C> withType(Class<?>... types) {
        super.withType(types);
        return this;
    }

    public T create() {
        // TODO klass if passed in context... remove from create method arg?


        final int suppliedTypeArguments = genericTypes == null ? 0 : genericTypes.length;

        // check if klass is generic, check if number of genericTypes specified matches the number of TypeVariables
        if (klass.getTypeParameters().length != suppliedTypeArguments) {
            // TODO improve error message with actual arguments included since we know the types...
            throw new InstancioException("Generic class " + klass.getName() + " has " + klass.getTypeParameters().length
                    + " type parameters: " + Arrays.toString(klass.getTypeParameters())
                    + ". Please specify all type parameters using 'withType(Class... types)`");
        }


        Map<Type, Class<?>> rootTypeMap = new HashMap<>();
        TypeVariable<Class<T>>[] typeParameters = klass.getTypeParameters();

        for (int i = 0; i < suppliedTypeArguments; i++) {
            final TypeVariable<?> typeParameter = typeParameters[i];
            final Class<?> actualType = genericTypes[i];
            rootTypeMap.put(typeParameter, actualType);
        }

        InstancioContext context = new InstancioContext(
                klass, exclusions, nullables, fieldValueGenerators, classValueGenerators, rootTypeMap);
        InstancioDriver driver = new InstancioDriver(context);
        return driver.create(klass);
    }
}
