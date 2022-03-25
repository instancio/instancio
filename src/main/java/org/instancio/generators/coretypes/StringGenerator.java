package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.GeneratedHints;
import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.util.Verify;

public class StringGenerator extends AbstractRandomGenerator<String> implements StringGeneratorSpec {

    private int minLength;
    private int maxLength;
    private boolean nullable;
    private boolean allowEmpty;
    private String prefix = "";

    public StringGenerator(final ModelContext<?> context) {
        super(context);

        final Settings settings = context.getSettings();
        this.minLength = settings.get(Setting.STRING_MIN_LENGTH);
        this.maxLength = settings.get(Setting.STRING_MAX_LENGTH);
        this.nullable = settings.get(Setting.STRING_NULLABLE);
        this.allowEmpty = settings.get(Setting.STRING_ALLOW_EMPTY);
    }

    @Override
    public StringGeneratorSpec prefix(final String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public StringGeneratorSpec allowEmpty() {
        this.allowEmpty = true;
        return this;
    }

    @Override
    public StringGeneratorSpec nullable() {
        this.nullable = true;
        return this;
    }

    @Override
    public StringGeneratorSpec minLength(final int length) {
        Verify.isTrue(length >= 0, "Length cannot be negative: %s", length);
        this.minLength = length;
        this.maxLength = Math.max(length, maxLength);
        return this;
    }

    @Override
    public StringGeneratorSpec maxLength(final int length) {
        Verify.isTrue(length >= 0, "Length cannot be negative: %s", length);
        this.maxLength = length;
        this.minLength = Math.min(minLength, length);
        return this;
    }

    @Override
    public String generate() {
        if (allowEmpty && random().oneInTenTrue()) {
            return "";
        }
        if (nullable && random().oneInTenTrue()) {
            return null;
        }
        return prefix + random().alphabetic(random().intBetween(minLength, maxLength + 1));
    }

    @Override
    public GeneratedHints getHints() {
        return GeneratedHints.builder()
                .nullableResult(nullable)
                .ignoreChildren(true)
                .build();
    }
}
