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
package org.instancio.test.features.generator.spatial;

import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class CoordinateGeneratorTest {
    private static Double create(final GeneratorSpecProvider<Double> spec) {
        return Instancio.of(Double.class)
                .generate(root(), spec)
                .create();
    }

    @Test
    void randomLatitudeByDefault() {
        final List<Double> coordinateList = Stream.generate(
                        () -> create(gen -> gen.spatial().coordinate()))
                .limit(Constants.SAMPLE_SIZE_DDD)
                .collect(Collectors.toList());

        coordinateList.forEach(d -> assertThat(d).isBetween(-90D, 90D));
    }

    @Test
    void validLatitude() {
        final List<Double> coordinateList = Stream.generate(
                        () -> create(gen -> gen.spatial().coordinate().lat()))
                .limit(Constants.SAMPLE_SIZE_DDD)
                .collect(Collectors.toList());

        coordinateList.forEach(d -> assertThat(d).isBetween(-90D, 90D));
    }

    @Test
    void validLongitude() {
        final List<Double> coordinateList = Stream.generate(
                        () -> create(gen -> gen.spatial().coordinate().lon()))
                .limit(Constants.SAMPLE_SIZE_DDD)
                .collect(Collectors.toList());

        coordinateList.forEach(d -> assertThat(d).isBetween(-180D, 180D));
    }

    @Test
    void nullableCoordinate() {
        final Stream<Double> result = Stream.generate(() -> create(gen -> gen.spatial().coordinate().nullable()))
                .limit(Constants.SAMPLE_SIZE_DDD);

        assertThat(result).containsNull();
    }

    @Test
    void nullableLatitude() {
        final Stream<Double> result = Stream.generate(() -> create(gen -> gen.spatial().coordinate().lat().nullable()))
                .limit(Constants.SAMPLE_SIZE_DDD);

        assertThat(result).containsNull();
    }

    @Test
    void nullableLongitude() {
        final Stream<Double> result = Stream.generate(() -> create(gen -> gen.spatial().coordinate().lon().nullable()))
                .limit(Constants.SAMPLE_SIZE_DDD);

        assertThat(result).containsNull();
    }
}
