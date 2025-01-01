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
package org.instancio.creation.generics.inheritance;

import org.instancio.test.support.pojo.generics.inheritance.WithGenericParent;
import org.instancio.test.support.pojo.generics.inheritance.WithGenericParent.GenericChild;
import org.instancio.test.support.pojo.generics.inheritance.WithGenericParent.GenericGrandChild;
import org.instancio.test.support.pojo.generics.inheritance.WithGenericParent.GenericParent;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
class WithGenericParentCreationTest extends CreationTestTemplate<WithGenericParent> {

    @Override
    protected void verify(final WithGenericParent result) {
        assertThat(result).isNotNull();

        //
        // Parent
        //
        final GenericParent<Long> genericParent = result.getGenericParent();

        assertThat(genericParent.getParentValue())
                .isInstanceOf(Long.class)
                .isNotNull();

        //
        // Child
        //
        final GenericChild<String> genericChild = result.getGenericChild();

        assertThat(genericChild.getParentValue())
                .isInstanceOf(String.class)
                .isNotBlank();

        assertThat(genericChild.getChildValue())
                .isInstanceOf(String.class)
                .isNotBlank();

        //
        // Grandchild
        //
        final GenericGrandChild<Month> genericGrandChild = result.getGenericGrandChild();

        assertThat(genericGrandChild.getParentValue())
                .isInstanceOf(Month.class)
                .isNotNull();

        assertThat(genericGrandChild.getChildValue())
                .isInstanceOf(Month.class)
                .isNotNull();
    }
}
