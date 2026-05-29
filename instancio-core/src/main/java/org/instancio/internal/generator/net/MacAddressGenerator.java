/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.internal.generator.net;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.MacAddressSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.jspecify.annotations.Nullable;

public class MacAddressGenerator extends AbstractGenerator<String> implements MacAddressSpec {

    public MacAddressGenerator(GeneratorContext context) {
        super(context);
    }

    @Nullable
    @Override
    public String apiMethod() {
        return "mac()";
    }

    @Nullable
    @Override
    protected String tryGenerateNonNull(final Random random) {
        if (isNullable()) return null;
        StringBuilder sb = new StringBuilder(17);
        for (int i = 0; i < 6; i++) {
            if (i > 0) sb.append(':');
            int octet = random.intRange(0, 255);
            sb.append(String.format("%02X", octet));
        }
        return sb.toString();
    }

    @Override
    public MacAddressGenerator nullable() {
        super.nullable();
        return this;
    }

}
