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
package org.instancio.test.features.generator.atomic;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.SupportedAtomicTypes;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag(Feature.ATOMIC_GENERATOR)
@ExtendWith(InstancioExtension.class)
class AtomicGeneratorsTest {

    @Test
    void generate() {
        final AtomicInteger atomicInteger = new AtomicInteger(1);
        final AtomicLong atomicLong = new AtomicLong(2);

        final SupportedAtomicTypes result = Instancio.of(SupportedAtomicTypes.class)
                .generate(all(AtomicInteger.class), gen -> gen.atomic().atomicInteger().range(atomicInteger, atomicInteger))
                .generate(all(AtomicLong.class), gen -> gen.atomic().atomicLong().range(atomicLong, atomicLong))
                .create();

        assertThat(result.getAtomicInteger().intValue()).isEqualTo(atomicInteger.intValue());
        assertThat(result.getAtomicLong().longValue()).isEqualTo(atomicLong.longValue());
    }
}