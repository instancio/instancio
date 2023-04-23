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
import org.instancio.generator.specs.EmailAsGeneratorSpec;
import org.instancio.generator.specs.EmailSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.specs.InternalLengthGeneratorSpec;
import org.instancio.support.Global;

import java.util.Locale;

/**
 * Internal generator used for generating email addresses for fields annotated
 * with {@code @Email} annotation from Jakarta Bean Validation API.
 */
public class EmailGenerator extends AbstractGenerator<String>
        implements EmailSpec, EmailAsGeneratorSpec, InternalLengthGeneratorSpec<String> {

    private static final String[] TLDS = {"com", "edu", "net", "org"};

    private int minLength = 7;
    private int maxLength = 24;

    public EmailGenerator() {
        super(Global.generatorContext());
    }

    public EmailGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "email()";
    }

    @Override
    public EmailGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public EmailGenerator nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    @Override
    public EmailGenerator length(final int min, final int max) {
        ApiValidator.isTrue(min >= 3,
                "email length must be at least 3 characters long");

        ApiValidator.isTrue(min <= max,
                "email length min must be less than or equal to max: (%s, %s)", min, max);

        minLength = min;
        maxLength = max;
        return this;
    }

    @Override
    public EmailGenerator length(final int length) {
        length(length, length);
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
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

