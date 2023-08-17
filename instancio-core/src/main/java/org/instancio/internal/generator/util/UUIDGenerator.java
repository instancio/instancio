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
package org.instancio.internal.generator.util;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.UUIDSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.support.Global;

import java.util.UUID;

public class UUIDGenerator extends AbstractGenerator<UUID> implements UUIDSpec {

    public UUIDGenerator() {
        this(Global.generatorContext());
    }

    public UUIDGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "uuid()";
    }

    @Override
    public UUIDGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public UUID tryGenerateNonNull(final Random random) {
        final byte[] randomBytes = new byte[16];
        for (int i = 0; i < randomBytes.length; i++) {
            randomBytes[i] = random.byteRange(Byte.MIN_VALUE, Byte.MAX_VALUE);
        }
        return UUID.nameUUIDFromBytes(randomBytes);
    }

}
