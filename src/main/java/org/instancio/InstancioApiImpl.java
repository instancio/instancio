package org.instancio;

import org.instancio.generator.ArrayGenerator;
import org.instancio.generator.Generator;
import org.instancio.model.InternalModel;
import org.instancio.model.ModelContext;
import org.instancio.util.ObjectUtils;
import org.instancio.util.TypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;

import static org.instancio.util.ReflectionUtils.getField;

class InstancioApiImpl<T> implements InstancioApi<T> {

    private final Class<T> rootClass;
    private final ModelContext.Builder<T> modelContextBuilder;

    InstancioApiImpl(Class<T> klass) {
        this.rootClass = klass;
        this.modelContextBuilder = ModelContext.builder(klass);
    }

    InstancioApiImpl(TypeTokenSupplier<T> typeToken) {
        final Type rootType = typeToken.get();
        this.rootClass = TypeUtils.getRawType(rootType);
        this.modelContextBuilder = ModelContext.builder(rootType);
    }

    InstancioApiImpl(Model<T> model) {
        final InternalModel<T> suppliedModel = (InternalModel<T>) model;
        final ModelContext<T> suppliedContext = suppliedModel.getModelContext();
        // copy context data to allow overriding
        this.modelContextBuilder = suppliedContext.toBuilder();
        this.rootClass =  suppliedContext.getRootClass();
    }

    protected void addTypeParameters(Class<?>... type) {
        modelContextBuilder.withRootTypeParameters(Arrays.asList(type));
    }

    @Override
    public InstancioApi<T> ignore(Binding binding) {
        if (binding.isFieldBinding()) {
            final Class<?> targetType = ObjectUtils.defaultIfNull(binding.getTargetType(), this.rootClass);
            modelContextBuilder.withIgnoredField(getField(targetType, binding.getFieldName()));
        } else {
            modelContextBuilder.withIgnoredClass(binding.getTargetType());
        }

        return this;
    }

    @Override
    public InstancioApi<T> withNullable(Binding target) {
        if (target.isFieldBinding()) {
            final Class<?> targetType = ObjectUtils.defaultIfNull(target.getTargetType(), this.rootClass);
            modelContextBuilder.withNullableField(getField(targetType, target.getFieldName()));
        } else {
            modelContextBuilder.withNullableClass(target.getTargetType());
        }
        return this;
    }

    @Override
    public <V> InstancioApi<T> with(Binding binding, Generator<V> generator) {
        if (binding.isFieldBinding()) {
            final Class<?> targetType = ObjectUtils.defaultIfNull(binding.getTargetType(), this.rootClass);
            final Field field = getField(targetType, binding.getFieldName());
            // XXX hacky work-around to set array type
            // FIXME this needs to be done in other API classes
            if (field.getType().isArray() && generator instanceof ArrayGenerator) {
                ((ArrayGenerator) generator).type(field.getType().getComponentType());
            }
            modelContextBuilder.withFieldGenerator(field, generator);
        } else {
            if (binding.getTargetType().isArray() && generator instanceof ArrayGenerator) {
                ((ArrayGenerator) generator).type(binding.getTargetType().getComponentType());
            }
            modelContextBuilder.withClassGenerator(binding.getTargetType(), generator);
        }
        return this;
    }

    @Override
    public InstancioApi<T> map(Class<?> baseClass, Class<?> subClass) {
        modelContextBuilder.withSubtypeMapping(baseClass, subClass);
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
