package org.instancio;

import org.instancio.generator.Generator;
import org.instancio.model.InternalModel;
import org.instancio.model.ModelContext;

import static org.instancio.util.ReflectionUtils.getField;

public class ModelCreationApi<T> implements CreationApi<T> {

    private final Class<?> rootClass;
    private final ModelContext.Builder modelContextBuilder;

    public ModelCreationApi(Model<T> model) {
        final InternalModel<T> suppliedModel = (InternalModel<T>) model;
        final ModelContext suppliedContext = suppliedModel.getModelContext();
        // copy context data to allow overriding
        this.modelContextBuilder = suppliedContext.toBuilder();
        this.rootClass = suppliedContext.getRootClass();
    }

    @Override
    public ModelCreationApi<T> ignore(Class<?> klass) {
        modelContextBuilder.withIgnoredClass(klass);
        return this;
    }

    @Override
    public ModelCreationApi<T> ignore(String field) {
        modelContextBuilder.withIgnoredField(getField(this.rootClass, field));
        return this;
    }

    @Override
    public ModelCreationApi<T> ignore(Class<?> klass, String field) {
        modelContextBuilder.withIgnoredField(getField(klass, field));
        return this;
    }

    @Override
    public ModelCreationApi<T> withNullable(Class<?> klass) {
        modelContextBuilder.withNullableClass(klass);
        return this;
    }

    @Override
    public ModelCreationApi<T> withNullable(String field) {
        modelContextBuilder.withNullableField(getField(this.rootClass, field));
        return this;
    }

    @Override
    public ModelCreationApi<T> withNullable(Class<?> klass, String field) {
        modelContextBuilder.withNullableField(getField(klass, field));
        return this;
    }

    @Override
    public ModelCreationApi<T> map(Class<?> baseClass, Class<?> subClass) {
        modelContextBuilder.withSubtypeMapping(baseClass, subClass);
        return this;
    }

    @Override
    public <V> ModelCreationApi<T> with(String field, Generator<V> generator) {
        modelContextBuilder.withFieldGenerator(getField(this.rootClass, field), generator);
        return this;
    }

    @Override
    public <V> ModelCreationApi<T> with(Class<?> klass, String field, Generator<V> generator) {
        modelContextBuilder.withFieldGenerator(getField(klass, field), generator);
        return this;
    }

    @Override
    public <V> ModelCreationApi<T> with(Class<V> klass, Generator<V> generator) {
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
