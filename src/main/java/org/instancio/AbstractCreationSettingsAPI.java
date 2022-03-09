package org.instancio;

import org.instancio.generator.ValueGenerator;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AbstractCreationSettingsAPI<T, C extends CreationSettingsAPI<T, C>> implements CreationSettingsAPI<T, C> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractCreationSettingsAPI.class);

    protected final Class<T> klass;
    protected final Set<Field> exclusions = new HashSet<>();
    protected final Set<Field> nullables = new HashSet<>();
    protected final Map<Field, ValueGenerator<?>> fieldValueGenerators = new HashMap<>();
    protected final Map<Class<?>, ValueGenerator<?>> classValueGenerators = new HashMap<>();
    protected Class<?>[] genericTypes;

    AbstractCreationSettingsAPI(Class<T> klass) {
        this.klass = klass;
    }

    @Override
    public AbstractCreationSettingsAPI<T, C> exclude(String... fields) {
        Verify.notEmpty(fields, "'exclude(String... fields)' requires at least one field to be specified");

        for (String field : fields) {
            final Field targetField = ReflectionUtils.getField(klass, field);
            exclusions.add(targetField);
            LOG.debug("Added '{}' to exclusion list", targetField);
        }
        return this;
    }

    @Override
    public <V> AbstractCreationSettingsAPI<T, C> with(String field, ValueGenerator<V> generator) {
        Verify.notNull(field, "'with(String field, ValueGenerator generator)' field must not be null");
        Verify.notNull(generator, "'with(String field, ValueGenerator generator)' generator must not be null");

        final Field targetField = ReflectionUtils.getField(klass, field);
        final ValueGenerator<?> oldGenerator = fieldValueGenerators.put(targetField, generator);
        if (oldGenerator != null)
            LOG.debug("Replaced '{}' for field: '{}'", oldGenerator.getClass().getName(), field);

        return this;
    }

    @Override
    public <V> AbstractCreationSettingsAPI<T, C> with(Class<V> klass, ValueGenerator<V> generator) {
        Verify.notNull(klass, "'with(Class klass, ValueGenerator generator)' class must not be null");
        Verify.notNull(generator, "'with(Class klass, ValueGenerator generator)' generator must not be null");

        final ValueGenerator<?> oldGenerator = classValueGenerators.put(klass, generator);
        if (oldGenerator != null)
            LOG.debug("Replaced '{}' for class: '{}'", oldGenerator.getClass().getName(), klass.getName());

        return this;
    }

    @Override
    public AbstractCreationSettingsAPI<T, C> withNullable(String fieldPath) {
        Verify.notNull(fieldPath, "Field must not be null");

        final Field field = ReflectionUtils.getField(klass, fieldPath);
        nullables.add(field);
        LOG.debug("Added " + fieldPath + " to nullables");
        return this;
    }

    @Override
    public AbstractCreationSettingsAPI<T, C> withType(Class<?>... types) {
        LOG.debug("Specified generic types: {}", Arrays.toString(types));
        this.genericTypes = types;
        return this;
    }
}
