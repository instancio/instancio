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
package org.instancio.internal.generators;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.CreditCardSpec;
import org.instancio.generators.FinanceSpecs;
import org.instancio.internal.generator.domain.finance.CreditCardNumberGenerator;

final class FinanceSpecsImpl implements FinanceSpecs {

    private final GeneratorContext context;

    FinanceSpecsImpl(final GeneratorContext context) {
        this.context = context;
    }

    @Override
    public CreditCardSpec creditCard() {
        return new CreditCardNumberGenerator(context);
    }

}
