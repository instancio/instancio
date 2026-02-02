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
package org.instancio.test.features.generator.custom;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Random;
import org.instancio.TypeToken;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allBooleans;

/**
 * A record generator may return a record with null fields.
 * Since records are immutable, verify there's no error trying
 * to generate and assign those fields.
 */
@FeatureTag(Feature.AFTER_GENERATE)
@ExtendWith(InstancioExtension.class)
class CustomGeneratorRecordWithNullFieldTest {

    private record Container(
            StringHolder stringHolder,
            Collection<String> collection,
            String string) {}

    private Model<Container> model(final boolean overwriteExistingValues, final AfterGenerate afterGenerate) {
        // Default AfterGenerate setting shouldn't matter since the Generator hint should take precedence.
        // Using a random value to ensure this is correct.
        final AfterGenerate defaultAfterGenerate = Instancio.gen().enumOf(AfterGenerate.class).get();

        return Instancio.of(Container.class)
                .withSettings(Settings.create()
                        .set(Keys.OVERWRITE_EXISTING_VALUES, overwriteExistingValues)
                        .set(Keys.AFTER_GENERATE_HINT, defaultAfterGenerate))
                .supply(all(Container.class), new Generator<Container>() {
                    @Override
                    public Container generate(final Random r) {
                        return new Container(null, null, null);
                    }

                    @Override
                    public Hints hints() {
                        return Hints.afterGenerate(afterGenerate);
                    }
                })
                .toModel();
    }

    private static Stream<Arguments> args() {
        return Instancio.ofCartesianProduct(new TypeToken<Pair<Boolean, AfterGenerate>>() {})
                .with(allBooleans(), true, false)
                .with(all(AfterGenerate.class), AfterGenerate.values())
                .create()
                .stream()
                .map(pair -> Arguments.of(pair.getLeft(), pair.getRight()));
    }

    @MethodSource("args")
    @ParameterizedTest
    void verify(boolean overWriteExistingValues, AfterGenerate afterGenerate) {
        final Container result = Instancio.create(model(overWriteExistingValues, afterGenerate));

        assertThat(result).isNotNull();
        assertThat(result.collection).isNull();
        assertThat(result.string).isNull();
        assertThat(result.stringHolder).isNull();
    }
}
