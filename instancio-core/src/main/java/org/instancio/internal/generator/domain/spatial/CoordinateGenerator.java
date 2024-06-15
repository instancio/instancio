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
package org.instancio.internal.generator.domain.spatial;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.CoordinateSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.lang.DoubleGenerator;

public class CoordinateGenerator extends AbstractGenerator<Double> implements CoordinateSpec {

    private final DoubleGenerator delegate;

    private CoordinateType type = CoordinateType.LATITUDE;

    public CoordinateGenerator(final GeneratorContext context) {
        super(context);
        delegate = new DoubleGenerator(context);
    }

    @Override
    public String apiMethod() {
        return "coordinate()";
    }

    @Override
    protected Double tryGenerateNonNull(final Random random) {
        if (type == CoordinateType.LONGITUDE) {
            return delegate.range(-180D, 180D).generate(random);
        } else {
            // Default to latitude
            return delegate.range(-90D, 90D).generate(random);
        }
    }

    @Override
    public CoordinateGenerator lat() {
        type = CoordinateType.LATITUDE;
        return this;
    }

    @Override
    public CoordinateGenerator lon() {
        type = CoordinateType.LONGITUDE;
        return this;
    }

    @Override
    public CoordinateGenerator nullable() {
        super.nullable();
        return this;
    }

    private enum CoordinateType {
        LATITUDE, LONGITUDE;
    }
}
