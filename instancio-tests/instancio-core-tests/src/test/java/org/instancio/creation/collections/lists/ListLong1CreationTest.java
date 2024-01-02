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
package org.instancio.creation.collections.lists;

import org.instancio.test.support.pojo.collections.lists.ListLong;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class ListLong1CreationTest extends CreationTestTemplate<ListLong> {

    @Override
    protected void verify(ListLong result) {
        assertThat(result.getList())
                .isNotEmpty()
                .hasOnlyElementsOfType(Long.class);
    }

}
