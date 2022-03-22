package org.instancio.internal;

import org.instancio.Binding;
import org.instancio.Generator;
import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.TypeTokenSupplier;
import org.instancio.generators.ArrayGenerator;
import org.instancio.internal.model.InternalModel;
import org.instancio.internal.model.ModelContext;
import org.instancio.util.ObjectUtils;
import org.instancio.util.TypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;

import static org.instancio.util.ReflectionUtils.getField;

public class InstancioApiImpl<T> implements InstancioApi<T> {

    private final Class<T> rootClass;
    private final ModelContext.Builder<T> modelContextBuilder;

    public InstancioApiImpl(final Class<T> klass) {
        this.rootClass = klass;
        this.modelContextBuilder = ModelContext.builder(klass);
    }

    public InstancioApiImpl(final TypeTokenSupplier<T> typeToken) {
        final Type rootType = typeToken.get();
        this.rootClass = TypeUtils.getRawType(rootType);
        this.modelContextBuilder = ModelContext.builder(rootType);
    }

    public InstancioApiImpl(final Model<T> model) {
        final InternalModel<T> suppliedModel = (InternalModel<T>) model;
        final ModelContext<T> suppliedContext = suppliedModel.getModelContext();
        // copy context data to allow overriding
        this.modelContextBuilder = suppliedContext.toBuilder();
        this.rootClass = suppliedContext.getRootClass();
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
