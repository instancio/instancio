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
package org.instancio.internal.generator.lang;

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.EmptyEnum;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@NonDeterministicTag
@FeatureTag(Feature.ENUM_GENERATOR)
class EnumGeneratorTest {

    private static final Random random = new DefaultRandom();
    private static final GeneratorContext context = new GeneratorContext(Settings.create(), random);

    enum SingleValueEnum {ONLY}

    @Test
    void apiMethod() {
        final EnumGenerator<Gender> generator = new EnumGenerator<>(context, Gender.class);
        assertThat(generator.apiMethod()).isEqualTo("enumOf()");
    }

    @Test
    void generate() {
        final Generator<Gender> generator = new EnumGenerator<>(context, Gender.class);
        assertThat(generator.generate(random)).isIn((Object[]) Gender.values());
    }

    @Test
    void generateEmptyEnum() {
        final Generator<EmptyEnum> generator = new EnumGenerator<>(context, EmptyEnum.class);
        assertThat(generator.generate(random)).isNull();
    }

    @Test
    void generateIncludesAllValues() {
        final EnumGenerator<Gender> generator = new EnumGenerator<>(context, Gender.class);
        assertThat(IntStream.range(1, 500).mapToObj(i -> generator.generate(random))).contains(Gender.values());
    }

    @RepeatedTest(3)
    void excluding() {
        final EnumGenerator<Gender> generator = new EnumGenerator<>(context, Gender.class);
        generator.excluding(Gender.MALE, Gender.FEMALE);
        assertThat(generator.generate(random)).isEqualTo(Gender.OTHER);
    }

    @Test
    void excludingWithEmptyArgs() {
        final EnumGenerator<SingleValueEnum> generator = new EnumGenerator<>(context, SingleValueEnum.class);
        generator.excluding();
        assertThat(generator.generate(random)).isEqualTo(SingleValueEnum.ONLY);
    }

    @Test
    void nullable() {
        final EnumGenerator<Gender> generator = new EnumGenerator<>(context, Gender.class);
        generator.nullable();

        assertThat(IntStream.range(1, 500).mapToObj(i -> generator.generate(random)))
                .containsNull();
    }

    @Test
    void validation() {
        final EnumGenerator<Gender> generator = new EnumGenerator<>(context, Gender.class);

        assertThatThrownBy(() -> generator.excluding((Gender[]) null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Excluded values must not be null: excluding()");

        assertThatThrownBy(() -> new EnumGenerator<>(context, null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Enum class must not be null");
    }
}
