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
 * Generator for unsigned proto int fields (uint32, fixed32).
 */
public final class ProtoUnsignedIntGenerator implements Generator<Integer> {

    private final int min;
    private final int max;

    public ProtoUnsignedIntGenerator(final Settings settings) {
        this.max = settings.get(Keys.INTEGER_MAX);

        if (max < 0) {
            throw Fail.withUsageError(ProtoErrorMessageUtils.unsignedFieldNegativeMax(
                    Keys.INTEGER_MAX, max, "uint32, fixed32"));
        }
        this.min = Math.max(0, settings.get(Keys.INTEGER_MIN));

    }

    @Override
    public Integer generate(final Random random) {
        return random.intRange(min, max);
    }

    @Override
    public Hints hints() {
        return Constants.DO_NOT_MODIFY_HINT;
    }
}
