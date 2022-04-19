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
package org.instancio.junit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class InstancioArgumentsProviderTest {

    private final InstancioArgumentsProvider provider = new InstancioArgumentsProvider();

    @Test
    void provideArguments() throws Exception {
        final Method method = InstancioArgumentsProviderTest.class.getDeclaredMethod("dummy");
        final InstancioSource instancioSource = method.getAnnotation(InstancioSource.class);

        // Methods under test
        provider.accept(instancioSource);
        final Stream<? extends Arguments> stream = provider.provideArguments(mock(ExtensionContext.class));

        assertThat(stream.collect(toList()))
                .hasSize(1)
                .extracting(Arguments::get)
                .allSatisfy(params -> {
                    assertThat(params).hasSize(2);
                    assertThat(params[0]).isInstanceOf(String.class);
                    assertThat(params[1]).isInstanceOf(Integer.class);
                });
    }

    @InstancioSource({String.class, Integer.class})
    private void dummy() {
    }
}
