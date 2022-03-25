package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.GeneratedHints;
import org.instancio.internal.model.ModelContext;
import org.instancio.util.Verify;

public abstract class AbstractRandomNumberGeneratorSpec<T extends Number>
        extends AbstractRandomGenerator<T> implements NumberGeneratorSpec<T> {

    protected T min;
    protected T max;
    protected boolean nullable;

    AbstractRandomNumberGeneratorSpec(final ModelContext<?> context, final T min, final T max, final boolean nullable) {
        super(context);
        this.min = min;
        this.max = max;
        this.nullable = nullable;
    }

    abstract T generateRandomValue();

    @Override
    public NumberGeneratorSpec<T> min(final T min) {
        this.min = Verify.notNull(min);
        return this;
    }

    @Override
    public NumberGeneratorSpec<T> max(final T max) {
        this.max = Verify.notNull(max);
        return this;
    }

    @Override
    public NumberGeneratorSpec<T> nullable() {
        this.nullable = true;
        return this;
    }

    @Override
    public T generate() {
        return nullable && random().oneInTenTrue() ? null : generateRandomValue();
    }

    @Override
    public GeneratedHints getHints() {
        return GeneratedHints.builder()
                .nullableResult(nullable)
                .ignoreChildren(true)
                .build();
    }
}
