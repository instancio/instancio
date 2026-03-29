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
package org.instancio.protobuf.internal.generator;

import com.google.protobuf.ProtocolMessageEnum;
import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.internal.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates a valid proto enum value,
 * excluding {@code UNRECOGNIZED} (number {@literal <} 0).
 */
public final class ProtoEnumGenerator implements Generator<Object> {

    private final List<Object> validConstants;

    public ProtoEnumGenerator(final Class<?> enumClass) {
        final List<Object> valid = new ArrayList<>();
        for (Object constant : enumClass.getEnumConstants()) {
            try {
                ((ProtocolMessageEnum) constant).getNumber();
                valid.add(constant);
            } catch (IllegalArgumentException ignored) {
                // Skip UNRECOGNIZED
            }
        }
        this.validConstants = Collections.unmodifiableList(valid);
    }

    @Override
    public Object generate(final Random random) {
        return random.oneOf(validConstants);
    }

    @Override
    public Hints hints() {
        return Constants.DO_NOT_MODIFY_HINT;
    }
}
