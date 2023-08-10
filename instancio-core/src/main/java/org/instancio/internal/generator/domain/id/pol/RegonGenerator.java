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
import org.instancio.generator.specs.RegonSpec;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.support.Global;

import java.util.List;

public class RegonGenerator extends WeightsModCheckGenerator implements RegonSpec {

    private RegonType type = RegonType.REGON9;

    public RegonGenerator() {
        this(Global.generatorContext());
    }

    public RegonGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "regon()";
    }

    @Override
    public RegonSpec type9() {
        type = RegonType.REGON9;
        return this;
    }

    @Override
    public RegonSpec type14() {
        type = RegonType.REGON14;
        return this;
    }

    @Override
    public RegonGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected List<Integer> weights() {
        return type.weights;
    }

    private enum RegonType {
        REGON9(8, 9, 2, 3, 4, 5, 6, 7),
        REGON14(2, 4, 8, 5, 0, 9, 7, 3, 6, 1, 2, 4, 8);

        private final List<Integer> weights;

        RegonType(final Integer... weights) {
            this.weights = CollectionUtils.asUnmodifiableList(weights);
        }
    }
}
