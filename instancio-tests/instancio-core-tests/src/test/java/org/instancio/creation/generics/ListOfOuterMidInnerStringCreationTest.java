/*
 * Copyright 2022-2023 the original author or authors.
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

import org.instancio.test.support.pojo.generics.outermidinner.ListOfOuterMidInnerString;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.test.support.util.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ListOfOuterMidInnerStringCreationTest extends CreationTestTemplate<ListOfOuterMidInnerString> {

    @Override
    protected void verify(ListOfOuterMidInnerString result) {
        assertThat(result.getRootList()).hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

        result.getRootList().forEach(outer -> {
            assertThat(outer).isNotNull();
            assertThat(outer.getOuterList()).hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

            outer.getOuterList().forEach(mid -> {
                assertThat(mid).isNotNull();
                assertThat(mid.getMidList()).hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

                mid.getMidList().forEach(inner -> {
                    assertThat(inner).isNotNull();
                    assertThat(inner.getInnerList())
                            .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                            .hasOnlyElementsOfType(String.class);
                });
            });
        });
    }
}
