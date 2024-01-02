/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.internal.generator.domain.id.usa;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.usa.SsnGeneratorSpec;
import org.instancio.generator.specs.usa.SsnSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.StringUtils;
import org.instancio.support.Global;
import org.jetbrains.annotations.VisibleForTesting;

public class SsnGenerator extends AbstractGenerator<String>
        implements SsnGeneratorSpec, SsnSpec {

    private static final int SSN_LENGTH = 9;

    private String separator;

    public SsnGenerator() {
        this(Global.generatorContext());
    }

    public SsnGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "ssn()";
    }

    @Override
    public SsnGenerator separator(final String separator) {
        this.separator = separator;
        return this;
    }

    @Override
    public SsnGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        final String ssn = generateValidSsn(random);

        if (separator == null) {
            return ssn;
        }

        return ssn.substring(0, 3) +
                separator +
                ssn.substring(3, 5) +
                separator +
                ssn.substring(5, SSN_LENGTH);
    }

    private String generateValidSsn(final Random random) {
        String ssn = random.digits(9);
        while (isInvalid(ssn)) ssn = random.digits(9);
        return ssn;
    }

    @VisibleForTesting
    static boolean isInvalid(final String ssn) {
        return (ssn.charAt(3) == '0' && ssn.charAt(4) == '0')
                || ssn.endsWith("0000")
                || StringUtils.startsWithAny(ssn,
                "000", "666", "9", "078051120", "219099999", "123456789");
    }
}
