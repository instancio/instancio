package org.instancio.internal;

import org.instancio.Binding;
import org.instancio.Generator;
import org.instancio.GeneratorSpec;
import org.instancio.Generators;
import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.TypeTokenSupplier;
import org.instancio.settings.Settings;
import org.instancio.internal.model.InternalModel;
import org.instancio.internal.model.ModelContext;
import org.instancio.util.ObjectUtils;
import org.instancio.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Function;

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
    public <V> InstancioApi<T> supply(Binding binding, Generator<V> generator) {
        modelContextBuilder.withGenerator(binding, generator);
        return this;
    }

    @Override
    public <V, S extends GeneratorSpec<V>> InstancioApi<T> generate(final Binding target, final Function<Generators, S> gen) {
        modelContextBuilder.withGeneratorSpec(target, gen);
        return this;
    }

    @Override
    public InstancioApi<T> map(Class<?> baseClass, Class<?> subClass) {
        modelContextBuilder.withSubtypeMapping(baseClass, subClass);
        return this;
    }

    @Override
    public InstancioApi<T> withSettings(final Settings settings) {
        modelContextBuilder.withSettings(settings);
        return this;
    }

    @Override
    public InstancioApi<T> withSeed(final int seed) {
        modelContextBuilder.withSeed(seed);
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
