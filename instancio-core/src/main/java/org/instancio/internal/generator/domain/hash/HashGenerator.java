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
package org.instancio.internal.generator.domain.hash;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.HashAsGeneratorSpec;
import org.instancio.generator.specs.HashSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.lang.StringGenerator;
import org.instancio.support.Global;

public class HashGenerator extends AbstractGenerator<String>
        implements HashSpec, HashAsGeneratorSpec {

    private final StringGenerator delegate;
    private Type type = Type.MD5;

    public HashGenerator() {
        this(Global.generatorContext());
    }

    public HashGenerator(final GeneratorContext context) {
        super(context);
        delegate = new StringGenerator(context);
    }

    @Override
    public String apiMethod() {
        return "hash()";
    }

    @Override
    public HashGenerator md5() {
        type = Type.MD5;
        return this;
    }

    @Override
    public HashGenerator sha1() {
        type = Type.SHA1;
        return this;

    }

    @Override
    public HashGenerator sha256() {
        type = Type.SHA256;
        return this;
    }

    @Override
    public HashGenerator sha512() {
        type = Type.SHA512;
        return this;
    }

    @Override
    public HashGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        return delegate.hex()
                .length(type.length)
                .tryGenerateNonNull(random);
    }

    private enum Type {
        MD5(32),
        SHA1(40),
        SHA256(64),
        SHA512(128);

        private final int length;

        Type(final int length) {
            this.length = length;
        }
    }
}