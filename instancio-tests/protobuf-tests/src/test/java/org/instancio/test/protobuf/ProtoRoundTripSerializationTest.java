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
package org.instancio.test.protobuf;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.prototobuf.Proto;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class ProtoRoundTripSerializationTest {

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void supportedNumericTypes() throws Exception {
        final Proto.SupportedNumericTypes original = Instancio.create(Proto.SupportedNumericTypes.class);
        final Proto.SupportedNumericTypes deserialized = Proto.SupportedNumericTypes.parseFrom(original.toByteArray());

        assertThat(deserialized).isEqualTo(original);
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void person() throws Exception {
        final Proto.Person original = Instancio.create(Proto.Person.class);
        final Proto.Person deserialized = Proto.Person.parseFrom(original.toByteArray());

        assertThat(deserialized).isEqualTo(original);
    }
}
