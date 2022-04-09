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
package org.instancio.creation.misc;

import org.instancio.test.support.pojo.misc.WithFinalUnassignedLong;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@NonDeterministicTag
public class WithFinalUnassignedLongCreationTest extends CreationTestTemplate<WithFinalUnassignedLong> {

    @Override
    protected void verify(final WithFinalUnassignedLong result) {
        assertThat(result.getPrimitive()).isNotZero();
        assertThat(result.getWrapper()).isNotNull().isNotZero();
    }
}
