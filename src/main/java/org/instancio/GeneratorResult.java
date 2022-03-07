package org.instancio;

import java.util.List;
import java.util.StringJoiner;

class GeneratorResult<T> {
    private final T value;
    private final List<CreateItem> fieldsToEnqueue;

    public GeneratorResult(T value, List<CreateItem> fieldsToEnqueue) {
        this.value = value;
        this.fieldsToEnqueue = fieldsToEnqueue;
    }

    public T getValue() {
        return value;
    }

    public List<CreateItem> getFieldsToEnqueue() {
        return fieldsToEnqueue;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GeneratorResult.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .add("fieldsToEnqueue=" + fieldsToEnqueue)
                .toString();
    }
}
