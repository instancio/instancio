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
package org.instancio.creation.generics;

import org.instancio.pojo.generics.foobarbaz.Bar;
import org.instancio.pojo.generics.foobarbaz.Baz;
import org.instancio.pojo.generics.foobarbaz.Foo;
import org.instancio.pojo.generics.foobarbaz.ListFooListBarListBazListString;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ListFooListBarListBazListStringCreationTest extends CreationTestTemplate<ListFooListBarListBazListString> {

    @Override
    protected void verify(ListFooListBarListBazListString result) {
        final List<Foo<List<Bar<List<Baz<List<String>>>>>>> list = result.getListOfFoo_ListOfBar_ListOfBaz_ListOfString();

        assertThat(list)
                // List<Foo>
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .hasOnlyElementsOfType(Foo.class);

        assertThat(list)
                .extracting(Foo::getOtherFooValue)
                .allSatisfy(it -> assertThat(it).isExactlyInstanceOf(Object.class));

        assertThat(list)
                // Foo
                .allSatisfy(foo -> assertThat(foo.getFooValue())
                        .isInstanceOf(List.class)
                        .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                        .hasOnlyElementsOfType(Bar.class)
                        // Bar
                        .allSatisfy(bar -> assertThat(bar.getBarValue())
                                .isInstanceOf(List.class)
                                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                                .hasOnlyElementsOfType(Baz.class)
                                // Baz
                                .allSatisfy(baz -> assertThat(baz.getBazValue())
                                        .isInstanceOf(List.class)
                                        .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                                        .hasOnlyElementsOfType(String.class))));

    }

}
