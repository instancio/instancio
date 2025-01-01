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
package org.instancio.creation.generics;

import org.instancio.test.support.pojo.generics.NestedMaps;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.test.support.util.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class NestedMapsCreationTest extends CreationTestTemplate<NestedMaps<String, Integer>> {

    @Override
    protected void verify(NestedMaps<String, Integer> result) {
        assertMap1(result.getMap1());
        assertMap2(result.getMap2());
    }

    private void assertMap1(Map<Long, Map<String, Boolean>> map1) {
        assertThat(map1).hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);
        assertThat(map1.keySet())
                .hasOnlyElementsOfType(Long.class)
                .doesNotContainNull();

        assertThat(map1.values())
                .hasOnlyElementsOfType(Map.class)
                .doesNotContainNull()
                .allSatisfy(nestedMap -> {

                    assertThat(nestedMap).hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

                    assertThat(nestedMap.keySet())
                            .hasOnlyElementsOfType(String.class)
                            .doesNotContainNull();

                    assertThat(nestedMap.values())
                            .hasOnlyElementsOfType(Boolean.class)
                            .doesNotContainNull();
                });
    }

    private void assertMap2(Map<?, ? extends Map<?, Boolean>> map1) {
        // Map<OKEY, Map<IKEY, Boolean>> map2

        assertThat(map1).hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);
        assertThat(map1.keySet())
                .hasOnlyElementsOfType(String.class)
                .doesNotContainNull();

        assertThat(map1.values())
                .hasOnlyElementsOfType(Map.class)
                .doesNotContainNull()
                .allSatisfy(nestedMap -> {

                    assertThat(nestedMap).hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

                    assertThat(nestedMap.keySet())
                            .hasOnlyElementsOfType(Integer.class)
                            .doesNotContainNull();

                    assertThat(nestedMap.values())
                            .hasOnlyElementsOfType(Boolean.class)
                            .doesNotContainNull();
                });
    }
}
