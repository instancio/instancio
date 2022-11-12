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
import org.instancio.internal.random.DefaultRandom;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@NonDeterministicTag
@FeatureTag(Feature.ENUM_GENERATOR)
class EnumGeneratorTest {

    private static final Random random = new DefaultRandom();

    enum EmptyEnum {}

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

}
