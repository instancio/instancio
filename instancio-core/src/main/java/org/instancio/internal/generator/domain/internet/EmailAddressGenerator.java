/*
 * Copyright 2022-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.internal.generator.domain.internet;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.NullableGeneratorSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.specs.InternalLengthGeneratorSpec;
import org.instancio.internal.util.Verify;

import java.util.Locale;

/**
 * Internal generator used for generating email addresses for fields annotated
 * with {@code @Email} annotation from Jakarta Bean Validation API.
 */
public class EmailAddressGenerator extends AbstractGenerator<String>
        implements NullableGeneratorSpec<String>, InternalLengthGeneratorSpec<String> {

    private static final String[] TLDS = {"com", "edu", "net", "org"};

    private int minLength = 7;
    private int maxLength = 24;

    public EmailAddressGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    public EmailAddressGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public EmailAddressGenerator nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    @Override
    public EmailAddressGenerator length(final int min, final int max) {
        Verify.isTrue(min >= 3, "Email length must be at least 3 characters long");
        Verify.isTrue(min <= max, "Min must be less than or equal to max");

        minLength = min;
        maxLength = max;
        return this;
    }

    public EmailAddressGenerator length(final int length) {
        length(length, length);
        return this;
    }

    @Override
    public String generate(final Random random) {
        if (random.diceRoll(isNullable())) {
            return null;
        }

        final int len = random.intRange(minLength, maxLength);

        // If length is at least 7, can generate with TLD: x@y.com
        if (len >= 7) {
            final String tld = "." + random.oneOf(TLDS);
            final int rem = len - tld.length() - 1; // -1 for @
            return emailWithoutTld(random, rem) + tld;
        } else {
            // Generate without TLD: x@y
            final int rem = len - 1; // -1 for @
            return emailWithoutTld(random, rem);
        }
    }

    private static String emailWithoutTld(final Random random, final int length) {
        final int localPartLen = random.intRange(1, length - 1);
        final int domainLen = length - localPartLen;
        final String localPart = random.alphanumeric(localPartLen).toLowerCase(Locale.ROOT);
        final String domain = random.alphanumeric(domainLen).toLowerCase(Locale.ROOT);
        return localPart + '@' + domain;
    }
}

