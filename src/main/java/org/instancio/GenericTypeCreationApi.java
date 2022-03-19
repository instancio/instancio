package org.instancio;

import org.instancio.generator.Generator;
import org.instancio.model.InternalModel;
import org.instancio.model.ModelContext;
import org.instancio.util.TypeUtils;

import java.lang.reflect.Type;

import static org.instancio.util.ReflectionUtils.getField;

public class GenericTypeCreationApi<T> implements CreationApi<T> {

    private final Class<T> rootClass;
    private final ModelContext.Builder modelContextBuilder;

    public GenericTypeCreationApi(TypeTokenSupplier<T> tTypeToken) {
        final Type rootType = tTypeToken.get();
        this.rootClass = TypeUtils.getRawType(rootType);
        this.modelContextBuilder = ModelContext.builder(rootType);
    }

    @Override
    public GenericTypeCreationApi<T> ignore(Class<?> klass) {
        modelContextBuilder.withIgnoredClass(klass);
        return this;
    }

    @Override
    public GenericTypeCreationApi<T> ignore(String field) {
        modelContextBuilder.withIgnoredField(getField(this.rootClass, field));
        return this;
    }

    @Override
    public GenericTypeCreationApi<T> ignore(Class<?> klass, String field) {
        modelContextBuilder.withIgnoredField(getField(klass, field));
        return this;
    }

    @Override
    public GenericTypeCreationApi<T> withNullable(Class<?> klass) {
        modelContextBuilder.withNullableClass(klass);
        return this;
    }

    @Override
    public GenericTypeCreationApi<T> withNullable(String field) {
        modelContextBuilder.withNullableField(getField(this.rootClass, field));
        return this;
    }

    @Override
    public GenericTypeCreationApi<T> withNullable(Class<?> klass, String field) {
        modelContextBuilder.withNullableField(getField(klass, field));
        return this;
    }

    @Override
    public GenericTypeCreationApi<T> map(Class<?> baseClass, Class<?> subClass) {
        modelContextBuilder.withSubtypeMapping(baseClass, subClass);
        return this;
    }

    @Override
    public <V> GenericTypeCreationApi<T> with(String field, Generator<V> generator) {
        modelContextBuilder.withFieldGenerator(getField(this.rootClass, field), generator);
        return this;
    }

    @Override
    public <V> GenericTypeCreationApi<T> with(Class<?> klass, String field, Generator<V> generator) {
        modelContextBuilder.withFieldGenerator(getField(klass, field), generator);
        return this;
    }

    @Override
    public <V> GenericTypeCreationApi<T> with(Class<V> klass, Generator<V> generator) {
        modelContextBuilder.withClassGenerator(klass, generator);
        return this;
    }

    @Override
    public Model<T> toModel() {
        return createModel();
    }

    @Override
    public T create() {
        final InstancioDriver instancioDriver = new InstancioDriver(createModel());
        return instancioDriver.createEntryPoint();
    }

    private InternalModel<T> createModel() {
        return new InternalModel<>(modelContextBuilder.build());
    }
}
