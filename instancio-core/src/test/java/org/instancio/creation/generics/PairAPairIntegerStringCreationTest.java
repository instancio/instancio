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
package org.instancio.creation.generics;

import org.instancio.pojo.generics.PairAPairIntegerString;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class PairAPairIntegerStringCreationTest extends CreationTestTemplate<PairAPairIntegerString<UUID>> {

    @Override
    protected void verify(PairAPairIntegerString<UUID> result) {
        assertThat(result.getPairAPairIntegerString()).isInstanceOf(Pair.class).satisfies(pair -> {
            assertThat(pair.getLeft()).isInstanceOf(UUID.class);
            assertThat(pair.getRight()).isInstanceOf(Pair.class).satisfies(nestedPair -> {
                assertThat(nestedPair.getLeft()).isInstanceOf(Integer.class);
                assertThat(nestedPair.getRight()).isInstanceOf(String.class);
            });
        });
    }
}
