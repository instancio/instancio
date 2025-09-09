/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.generator.domain.id.pol;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.pol.NipSpec;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.settings.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NipGenerator extends WeightsModCheckGenerator implements NipSpec {
    private static final Logger LOG = LoggerFactory.getLogger(NipGenerator.class);
    private static final List<Integer> NIP_WEIGHTS = CollectionUtils.asUnmodifiableList(6, 5, 7, 2, 3, 4, 5, 6, 7);
    private static final String FALLBACK = "123456321";

    public NipGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "nip()";
    }

    @Override
    public NipGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String payload(final Random random) {
        final int maxGenerationAttempts = getContext().getSettings().get(Keys.MAX_GENERATION_ATTEMPTS);
        for (int count = 0; count < maxGenerationAttempts; count++) {
            final String payload = super.payload(random);
            if (modulo(payload) < 10) {
                return payload;
            }
        }
        LOG.atWarn()
                .setMessage("Max attempts reached {} (configurable with the setting {}), " +
                        "returning a static fallback to guarantee a valid Nip code")
                .addArgument(maxGenerationAttempts)
                .addArgument(Keys.MAX_GENERATION_ATTEMPTS::propertyKey)
                .log();
        return FALLBACK;
    }

    @Override
    protected List<Integer> weights() {
        return NIP_WEIGHTS;
    }
}
