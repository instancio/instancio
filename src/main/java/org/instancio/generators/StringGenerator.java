package org.instancio.generators;

import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

public class StringGenerator extends AbstractGenerator<String> implements StringGeneratorSpec {

    private int minLength = 4;
    private int maxLength = 10;
    private boolean allowEmpty = false;
    private String prefix = "";

    public StringGenerator(final RandomProvider random) {
        super(random);
    }

    @Override
    public StringGenerator prefix(final String prefix) { // TODO return StringGeneratorSpec instead
        this.prefix = prefix;
        return this;
    }

    @Override
    public StringGenerator allowEmpty() {
        this.allowEmpty = true;
        return this;
    }

    @Override
    public StringGenerator min(final int length) {
        Verify.isTrue(length >= 0, "Length cannot be negative: %s", length);
        this.minLength = length;
        this.maxLength = Math.max(length, maxLength);
        return this;
    }

    @Override
    public StringGenerator max(final int length) {
        Verify.isTrue(length >= 0, "Length cannot be negative: %s", length);
        this.maxLength = length;
        this.minLength = Math.min(minLength, length);
        return this;
    }

    @Override
    public String generate() {
        if (allowEmpty && random().trueOrFalse()) {
            return "";
        }
        return prefix + random().alphabetic(random().intBetween(minLength, maxLength + 1));
    }
}
