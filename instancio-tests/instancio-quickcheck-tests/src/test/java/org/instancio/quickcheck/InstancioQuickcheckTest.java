/*
 *  Copyright 2022-2023 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.quickcheck;

import java.util.stream.IntStream;

import org.instancio.Instancio;
import org.instancio.quickcheck.api.ForAll;
import org.instancio.quickcheck.api.Property;
import org.instancio.quickcheck.api.artbitrary.Arbitrary;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

public class InstancioQuickcheckTest {
    @DisplayName("Test positive integers")
    @Property(samples = 100)
    public void positiveIntegers(@ForAll Integer i) {
        assertThat(i).isGreaterThan(0);
    }
    
    @Property(samples = 100)
    public void oddIntegers(@ForAll("odds") Integer i) {
        assertThat(i % 2).isEqualTo(0);
    }
    
    public Arbitrary<Integer> odds() {
        return Arbitrary.fromStream(IntStream
            .generate(() -> Instancio.create(Integer.class))
            .filter(i -> (i & 1) == 0));
    }
}
