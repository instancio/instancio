package org.instancio.generator;

import org.instancio.exception.InstancioException;
import org.instancio.util.Verify;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionGenerator implements Generator<Collection<?>> {

    private int collectionSize = 2;
    private Class<?> collectionType = ArrayList.class;

    public CollectionGenerator size(final int size) {
        Verify.isTrue(size >= 0, "Size cannot be negative: " + size);
        this.collectionSize = size;
        return this;
    }

    public CollectionGenerator type(Class<?> collectionType) {
        this.collectionType = Verify.notNull(collectionType, "Collection type must not be null");
        return this;
    }


    public int getCollectionSize() {
        return collectionSize;
    }

    @Override
    public Collection<?> generate() {
        try {
            return (Collection<?>) collectionType.newInstance();
        } catch (Exception ex) {
            throw new InstancioException(String.format("Error creating a collection instance of type: %s", collectionType), ex);
        }
    }

}
