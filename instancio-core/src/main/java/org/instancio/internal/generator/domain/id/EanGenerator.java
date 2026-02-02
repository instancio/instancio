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
package org.instancio.internal.generator.domain.id;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.EanSpec;
import org.instancio.internal.generator.checksum.BaseModCheckGenerator;

public class EanGenerator extends BaseModCheckGenerator implements EanSpec {

    private EanType type = EanType.EAN13;

    public EanGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "ean()";
    }

    @Override
    public EanGenerator type13() {
        type = EanType.EAN13;
        return this;
    }

    @Override
    public EanGenerator type8() {
        type = EanType.EAN8;
        return this;
    }

    @Override
    public EanGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected int payloadLength() {
        return type.length - 1;
    }

    @Override
    protected int even(final int position) {
        return 3;
    }

    @Override
    protected boolean sumDigits() {
        return false;
    }

    private enum EanType {
        EAN8(8),
        EAN13(13);

        private final int length;

        EanType(final int length) {
            this.length = length;
        }
    }
}

