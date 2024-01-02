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
package org.instancio.creation.collections.maps;

import org.instancio.test.support.pojo.collections.maps.MapIntegerString;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.test.support.util.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class MapIntegerStringCreationTest extends CreationTestTemplate<MapIntegerString> {

    @Override
    protected void verify(MapIntegerString result) {
        assertThat(result.getMap())
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

        assertThat(result.getMap().entrySet())
                .allSatisfy(entry -> {
                    assertThat(entry.getKey()).isInstanceOf(Integer.class);
                    assertThat(entry.getValue()).isInstanceOf(String.class);
                });

    }

}
