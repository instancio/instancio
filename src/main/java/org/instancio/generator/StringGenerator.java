package org.instancio.generator;

import org.instancio.util.Random;
import org.instancio.util.Verify;

public class StringGenerator implements Generator<String> {

    private int minLength = 4;
    private int maxLength = 10;
    private boolean allowEmpty = false;
    private String prefix = "";

    public StringGenerator prefix(final String prefix) {
        this.prefix = prefix;
        return this;
    }

    public StringGenerator allowEmpty() {
        this.allowEmpty = true;
        return this;
    }

    public StringGenerator min(final int length) {
        Verify.isTrue(length >= 0, "Length cannot be negative: " + length);
        this.minLength = length;
        this.maxLength = Math.max(length, maxLength);
        return this;
    }

    public StringGenerator max(final int length) {
        Verify.isTrue(length >= 0, "Length cannot be negative: " + length);
        this.maxLength = length;
        this.minLength = Math.min(minLength, length);
        return this;
    }

    @Override
    public String generate() {
        if (allowEmpty && Random.trueOrFalse()) {
            return "";
        }
        return prefix + Random.alphabetic(Random.intBetween(minLength, maxLength + 1));
    }
}
