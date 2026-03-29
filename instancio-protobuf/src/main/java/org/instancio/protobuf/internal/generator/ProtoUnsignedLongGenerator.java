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

import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.Fail;
import org.instancio.protobuf.internal.util.ProtoErrorMessageUtils;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

/**
 * Generator for unsigned proto long fields (uint64, fixed64).
 */
public final class ProtoUnsignedLongGenerator implements Generator<Long> {

    private final long min;
    private final long max;

    public ProtoUnsignedLongGenerator(final Settings settings) {
        this.max = settings.get(Keys.LONG_MAX);
        if (max < 0L) {
            throw Fail.withUsageError(ProtoErrorMessageUtils.unsignedFieldNegativeMax(
                    Keys.LONG_MAX, max, "uint64, fixed64"));
        }
        this.min = Math.max(0L, settings.get(Keys.LONG_MIN));
    }

    @Override
    public Long generate(final Random random) {
        return random.longRange(min, max);
    }

    @Override
    public Hints hints() {
        return Constants.DO_NOT_MODIFY_HINT;
    }
}
