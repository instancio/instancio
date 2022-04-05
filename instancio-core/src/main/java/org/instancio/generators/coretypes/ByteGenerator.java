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
package org.instancio.generators.coretypes;

import org.instancio.GeneratorContext;
import org.instancio.settings.Setting;

public class ByteGenerator extends AbstractRandomComparableNumberGeneratorSpec<Byte> {

    public ByteGenerator(final GeneratorContext context) {
        super(context,
                context.getSettings().get(Setting.BYTE_MIN),
                context.getSettings().get(Setting.BYTE_MAX),
                context.getSettings().get(Setting.BYTE_NULLABLE));
    }

    @Override
    protected Byte generateNonNullValue() {
        return random().byteBetween(min, max);
    }
}
