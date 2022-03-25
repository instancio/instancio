package org.instancio.generators.collections;

import org.instancio.exception.InstancioException;
import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.GeneratedHints;
import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;
import org.instancio.util.Verify;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionGenerator<T> extends AbstractRandomGenerator<Collection<T>> implements CollectionGeneratorSpec<T> {

    private int minSize;
    private int maxSize;
    private boolean nullable;
    private boolean nullableElements;
    private Class<?> type = ArrayList.class;

    public CollectionGenerator(final ModelContext<?> context) {
        super(context);
        this.minSize = context.getSettings().get(Setting.COLLECTION_MIN_SIZE);
        this.maxSize = context.getSettings().get(Setting.COLLECTION_MAX_SIZE);
        this.nullable = context.getSettings().get(Setting.COLLECTION_NULLABLE);
        this.nullableElements = context.getSettings().get(Setting.COLLECTION_ELEMENTS_NULLABLE);
    }

    @Override
    public CollectionGeneratorSpec<T> minSize(final int size) {
        Verify.isTrue(size >= 0, "Size cannot be negative: %s", size);
        this.minSize = size;
        this.maxSize = Math.max(minSize, maxSize);
        return this;
    }

    @Override
    public CollectionGeneratorSpec<T> maxSize(final int size) {
        Verify.isTrue(size >= 0, "Size cannot be negative: %s", size);
        this.maxSize = size;
        this.minSize = Math.min(minSize, maxSize);
        return this;
    }

    @Override
    public CollectionGeneratorSpec<T> nullable() {
        this.nullable = true;
        return this;
    }

    @Override
    public CollectionGeneratorSpec<T> nullableElements() {
        this.nullableElements = true;
        return this;
    }

    @Override
    public CollectionGeneratorSpec<T> type(final Class<?> type) {
        this.type = Verify.notNull(type, "Type must not be null");
        return this;
    }

    @Override
    public Collection<T> generate() {

        // XXX what to do if 'type' is not supplied by user?
        //
        // if it's a field or nested collection, should be able to default to ClassNode's type
        // can also make type mandatory and throw an error if it's not supplied

        try {
            // TODO
            return nullable && random().oneInTenTrue() ? null : (Collection<T>) type.newInstance();
        } catch (Exception ex) {
            throw new InstancioException(String.format("Error creating instance of: %s", type), ex);
        }
    }

    @Override
    public GeneratedHints getHints() {
        return GeneratedHints.builder()
                .dataStructureSize(random().intBetween(minSize, maxSize + 1))
                .ignoreChildren(false)
                .nullableResult(nullable)
                .nullableResult(nullableElements) // XXX
                .build();
    }
}
