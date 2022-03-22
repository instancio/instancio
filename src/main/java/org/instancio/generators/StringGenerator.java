package org.instancio.generators;

import org.instancio.GeneratorSpec;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

public class StringGenerator extends AbstractGenerator<String> {


    public static class StringGeneratorSpec implements GeneratorSpec {
        private int minLength = 4;
        private int maxLength = 10;
        private boolean allowEmpty = false;
        private String prefix = "";

        public StringGeneratorSpec prefix(final String prefix) {
            this.prefix = prefix;
            return this;
        }

        public StringGeneratorSpec allowEmpty() {
            this.allowEmpty = true;
            return this;
        }

        public StringGeneratorSpec min(final int length) {
            Verify.isTrue(length >= 0, "Length cannot be negative: %s", length);
            this.minLength = length;
            this.maxLength = Math.max(length, maxLength);
            return this;
        }

        public StringGeneratorSpec max(final int length) {
            Verify.isTrue(length >= 0, "Length cannot be negative: %s", length);
            this.maxLength = length;
            this.minLength = Math.min(minLength, length);
            return this;
        }
    }

    private int minLength = 4;
    private int maxLength = 10;
    private boolean allowEmpty = false;
    private String prefix = "";

    public StringGenerator(final RandomProvider random) {
        super(random);
    }


    public StringGenerator(final RandomProvider random, GeneratorSpec specs) {
        super(random);
        StringGeneratorSpec stringSpecs = (StringGeneratorSpec) specs;
        this.minLength = stringSpecs.minLength;
        this.maxLength = stringSpecs.maxLength;
        this.allowEmpty = stringSpecs.allowEmpty;
        this.prefix = stringSpecs.prefix;
    }

    public StringGenerator prefix(final String prefix) {
        this.prefix = prefix;
        return this;
    }

    public StringGenerator allowEmpty() {
        this.allowEmpty = true;
        return this;
    }

    public StringGenerator min(final int length) {
        Verify.isTrue(length >= 0, "Length cannot be negative: %s", length);
        this.minLength = length;
        this.maxLength = Math.max(length, maxLength);
        return this;
    }

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
