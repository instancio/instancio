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
package org.instancio.test.features.size;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.lists.ListString;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.SIZE, Feature.WITH_NULLABLE})
@ExtendWith(InstancioExtension.class)
class SizeWithNullableTest {

    private static final int SIZE = 7;

    @Test
    void sizeIsAppliedWhenCollectionIsNotNull() {
        final List<ListString> result = Instancio.ofList(ListString.class)
                .size(Constants.SAMPLE_SIZE_DD)
                .withNullable(field(ListString::getList))
                .size(field(ListString::getList), SIZE)
                .create();

        // Lists should either be null, or non-null with the expected size
        assertThat(result)
                .extracting(ListString::getList)
                .containsNull()
                .filteredOn(Objects::nonNull)
                .isNotEmpty()
                .allMatch(list -> list.size() == SIZE);
    }
}
