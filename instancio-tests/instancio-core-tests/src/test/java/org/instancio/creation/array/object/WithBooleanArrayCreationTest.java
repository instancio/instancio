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
package org.instancio.creation.array.object;

import org.instancio.test.support.pojo.arrays.primitive.WithBooleanArray;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.instancio.test.support.util.ArrayUtils;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;

public class WithBooleanArrayCreationTest extends ArrayCreationTestTemplate<WithBooleanArray> {

    @Override
    protected int minNumberOfGeneratedValues() {
        return 2;
    }

    @Override
    @NonDeterministicTag
    @NumberOfExecutions(20)
    protected void verify(WithBooleanArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
