/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.generator.lang;

import org.instancio.Generator;
import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.random.DefaultRandom;
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

    enum EmptyEnum {}

    enum SingleValueEnum {ONLY}

    @Test
    void generate() {
        final Generator<Gender> generator = new EnumGenerator<>(Gender.class);
        assertThat(generator.generate(random)).isIn((Object[]) Gender.values());
    }

    @Test
    void generateEmptyEnum() {
        final Generator<EmptyEnum> generator = new EnumGenerator<>(EmptyEnum.class);
        assertThat(generator.generate(random)).isNull();
    }

    @Test
    void generateIncludesAllValues() {
        final EnumGenerator<Gender> generator = new EnumGenerator<>(Gender.class);
        assertThat(IntStream.range(1, 500).mapToObj(i -> generator.generate(random))).contains(Gender.values());
    }

    @RepeatedTest(3)
    void excluding() {
        final EnumGenerator<Gender> generator = new EnumGenerator<>(Gender.class);
        generator.excluding(Gender.MALE, Gender.FEMALE);
        assertThat(generator.generate(random)).isEqualTo(Gender.OTHER);
    }

    @Test
    void excludingWithEmptyArgs() {
        final EnumGenerator<SingleValueEnum> generator = new EnumGenerator<>(SingleValueEnum.class);
        generator.excluding();
        assertThat(generator.generate(random)).isEqualTo(SingleValueEnum.ONLY);
    }

    @Test
    void nullable() {
        final EnumGenerator<Gender> generator = new EnumGenerator<>(Gender.class);
        generator.nullable();

        assertThat(IntStream.range(1, 500).mapToObj(i -> generator.generate(random)))
                .containsNull();
    }

    @Test
    void supports() {
        final EnumGenerator<Gender> generator = new EnumGenerator<>(Gender.class);
        assertThat(generator.supports(Gender.class)).isTrue();
        assertThat(generator.supports(SingleValueEnum.class)).isFalse();
    }

    @Test
    void validation() {
        final EnumGenerator<Gender> generator = new EnumGenerator<>(Gender.class);
        assertThatThrownBy(() -> generator.excluding(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Excluded values must not be null: excluding()");

        assertThatThrownBy(() -> new EnumGenerator<>(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Enum class must not be null");
    }
}
