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
package org.instancio.creation.cyclic;

import org.instancio.test.support.pojo.cyclic.IndirectCircularRef;
import org.instancio.test.support.tags.CyclicTag;
import org.instancio.testsupport.templates.AutoVerificationDisabled;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@CyclicTag
@AutoVerificationDisabled
public class IndirectCircularRefCreationTest extends CreationTestTemplate<IndirectCircularRef> {

    @Override
    protected void verify(IndirectCircularRef result) {
        assertThat(result.getStartA()).isNotNull()
                // A -> B
                .extracting(IndirectCircularRef.A::getB)
                .satisfies(b -> assertThat(b).isNotNull())
                // B -> C
                .extracting(IndirectCircularRef.B::getC)
                .satisfies(c -> assertThat(c).isNotNull())
                // C -> endA
                .extracting(IndirectCircularRef.C::getEndA)
                .satisfies(endA -> assertThat(endA).isNull());
    }

}
