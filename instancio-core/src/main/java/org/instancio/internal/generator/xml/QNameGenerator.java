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
package org.instancio.internal.generator.xml;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.AbstractGenerator;
import org.jspecify.annotations.Nullable;

import javax.xml.namespace.QName;

public class QNameGenerator extends AbstractGenerator<QName> {

    public QNameGenerator(final GeneratorContext context) {
        super(context);
    }

    @Nullable
    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    protected QName tryGenerateNonNull(final Random random) {
        final String localPart = random.upperCaseAlphabetic(8);
        final String prefix = random.upperCaseAlphabetic(3);
        return new QName("http://www.example.com/xml/namespace", localPart, prefix);
    }
}
