/*
 *  Copyright 2022-2023 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.support.pojo.generics;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.instancio.test.support.pojo.generics.basic.Triplet;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;

import java.util.List;

@Data
public class TripletAFooBarBazStringListOfB<A, B> {

    private Triplet<A, Foo<Bar<Baz<String>>>, List<B>> tripletA_FooBarBazString_ListOfB;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
