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
package org.instancio.test.features.generator.net;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.Inet4Address;
import java.net.InetAddress;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class InetAddressGeneratorTest {

    @ValueSource(classes = {InetAddress.class, Inet4Address.class})
    @ParameterizedTest
    void create(final Class<?> klass) {
        assertThat(Instancio.create(klass)).isNotNull();
    }
}