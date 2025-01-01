/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.tests.jpms.guava;

import com.google.common.collect.ImmutableList;
import com.google.common.net.InternetDomainName;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class JpmsGuavaTest {

    private static class GuavaPerson {
        private String name;
        private InternetDomainName website;
        private ImmutableList<String> hobbies;
    }

    @Test
    void create() {
        final GuavaPerson result = Instancio.create(GuavaPerson.class);

        assertThat(result.name).isNotBlank();

        assertThat(result.website.toString())
                .matches("[a-z]+\\.[a-z]+");

        assertThat(result.hobbies)
                .isNotEmpty()
                .isInstanceOf(ImmutableList.class)
                .allSatisfy(hobby -> assertThat(hobby).isNotBlank());
    }
}
