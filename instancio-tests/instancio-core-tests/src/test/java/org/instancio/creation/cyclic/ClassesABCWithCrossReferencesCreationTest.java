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
package org.instancio.creation.cyclic;

import org.instancio.test.support.pojo.cyclic.ClassesABCWithCrossReferences;
import org.instancio.test.support.tags.CyclicTag;
import org.instancio.testsupport.templates.AutoVerificationDisabled;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@CyclicTag
@AutoVerificationDisabled
public class ClassesABCWithCrossReferencesCreationTest extends CreationTestTemplate<ClassesABCWithCrossReferences> {

    @Override
    protected void verify(final ClassesABCWithCrossReferences result) {
        assertThat(result.getObjectA().getObjectA()).isNull();
        assertThat(result.getObjectA().getObjectB().getObjectA()).isNull();
        assertThat(result.getObjectA().getObjectB().getObjectB()).isNull();
        assertThat(result.getObjectA().getObjectC().getObjectA()).isNull();
        assertThat(result.getObjectA().getObjectC().getObjectC()).isNull();
        assertThat(result.getObjectA().getObjectB().getObjectC().getObjectA()).isNull();
        assertThat(result.getObjectA().getObjectB().getObjectC().getObjectB()).isNull();
        assertThat(result.getObjectA().getObjectB().getObjectC().getObjectC()).isNull();
        assertThat(result.getObjectA().getObjectC().getObjectB().getObjectA()).isNull();
        assertThat(result.getObjectA().getObjectC().getObjectB().getObjectB()).isNull();
        assertThat(result.getObjectA().getObjectC().getObjectB().getObjectC()).isNull();

        assertThat(result.getObjectB().getObjectB()).isNull();
        assertThat(result.getObjectB().getObjectA().getObjectA()).isNull();
        assertThat(result.getObjectB().getObjectA().getObjectB()).isNull();
        assertThat(result.getObjectB().getObjectC().getObjectB()).isNull();
        assertThat(result.getObjectB().getObjectC().getObjectC()).isNull();
        assertThat(result.getObjectB().getObjectA().getObjectC().getObjectA()).isNull();
        assertThat(result.getObjectB().getObjectA().getObjectC().getObjectB()).isNull();
        assertThat(result.getObjectB().getObjectA().getObjectC().getObjectC()).isNull();
        assertThat(result.getObjectB().getObjectC().getObjectA().getObjectA()).isNull();
        assertThat(result.getObjectB().getObjectC().getObjectA().getObjectB()).isNull();
        assertThat(result.getObjectB().getObjectC().getObjectA().getObjectC()).isNull();

        assertThat(result.getObjectC().getObjectC()).isNull();
        assertThat(result.getObjectC().getObjectA().getObjectA()).isNull();
        assertThat(result.getObjectC().getObjectA().getObjectC()).isNull();
        assertThat(result.getObjectC().getObjectB().getObjectB()).isNull();
        assertThat(result.getObjectC().getObjectB().getObjectC()).isNull();
        assertThat(result.getObjectC().getObjectA().getObjectB().getObjectA()).isNull();
        assertThat(result.getObjectC().getObjectA().getObjectB().getObjectB()).isNull();
        assertThat(result.getObjectC().getObjectA().getObjectB().getObjectC()).isNull();
        assertThat(result.getObjectC().getObjectB().getObjectA().getObjectA()).isNull();
        assertThat(result.getObjectC().getObjectB().getObjectA().getObjectB()).isNull();
        assertThat(result.getObjectC().getObjectB().getObjectA().getObjectC()).isNull();
    }
}
