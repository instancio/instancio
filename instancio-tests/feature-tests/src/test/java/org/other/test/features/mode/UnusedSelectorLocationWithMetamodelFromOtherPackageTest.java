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
package org.other.test.features.mode;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Fail.fail;
import static org.instancio.Select.all;
import static org.instancio.test.support.UnusedSelectorsAssert.assertThrowsUnusedSelectorException;

@FeatureTag({Feature.MODE, Feature.SELECTOR})
class UnusedSelectorLocationWithMetamodelFromOtherPackageTest {

    @Test
    void unused() {
        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .supply(OtherPojo_.foo, () -> fail("should not be called"))
                .ignore(all(
                        OtherPojo_.bar,
                        OtherPojo_.baz));

        assertThrowsUnusedSelectorException(api)
                .hasUnusedSelectorCount(3)
                .unusedGeneratorSelectorAt(OtherPojo_.foo, line(35))
                .unusedIgnoreSelectorAt(OtherPojo_.bar, line(36))
                .unusedIgnoreSelectorAt(OtherPojo_.baz, line(36));
    }

    private static String line(final int line) {
        return String.format("at org.other.test.features.mode.UnusedSelectorLocationWithMetamodelFromOtherPackageTest" +
                ".unused(UnusedSelectorLocationWithMetamodelFromOtherPackageTest.java:%s)", line);
    }
}
