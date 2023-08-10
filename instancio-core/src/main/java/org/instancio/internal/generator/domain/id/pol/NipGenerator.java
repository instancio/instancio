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
package org.instancio.internal.generator.domain.id.pol;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.NipSpec;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.support.Global;

import java.util.List;

public class NipGenerator extends WeightsModCheckGenerator implements NipSpec {

    private static final List<Integer> NIP_WEIGHTS = CollectionUtils.asUnmodifiableList(6, 5, 7, 2, 3, 4, 5, 6, 7);

    public NipGenerator() {
        this(Global.generatorContext());
    }

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
    protected List<Integer> weights() {
        return NIP_WEIGHTS;
    }
}
