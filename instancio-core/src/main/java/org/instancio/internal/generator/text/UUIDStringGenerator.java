/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.internal.generator.text;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.UUIDStringGeneratorSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.util.UUIDGenerator;

import java.util.Locale;

public class UUIDStringGenerator extends AbstractGenerator<String> implements UUIDStringGeneratorSpec {
    private boolean isUpperCase;
    private boolean isWithoutDashes;

    private final UUIDGenerator delegate;

    public UUIDStringGenerator(final GeneratorContext context) {
        super(context);
        delegate = new UUIDGenerator(context);
    }

    @Override
    public UUIDStringGeneratorSpec upperCase() {
        isUpperCase = true;
        return this;
    }

    @Override
    public UUIDStringGeneratorSpec withoutDashes() {
        isWithoutDashes = true;
        return this;
    }

    @Override
    public String generate(final Random random) {
        String uuid = delegate.generate(random).toString();
        if (isUpperCase) {
            uuid = uuid.toUpperCase(Locale.ROOT);
        }
        if (isWithoutDashes) {
            uuid = uuid.replace("-", "");
        }
        return uuid;
    }
}
