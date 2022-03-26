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
package org.instancio.model;

import org.instancio.internal.model.Node;
import org.instancio.pojo.generics.foobarbaz.Bar;
import org.instancio.pojo.generics.foobarbaz.Baz;
import org.instancio.pojo.generics.foobarbaz.Foo;
import org.instancio.pojo.generics.foobarbaz.FooBarBazContainer;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;
import org.junit.platform.commons.util.CollectionUtils;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.CollectionUtils.getOnlyElement;

class FooBarBazContainerModelTest extends ModelTestTemplate<FooBarBazContainer> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(FooBarBazContainer.class)
                .hasChildrenOfSize(1);

        final Node itemNode = CollectionUtils.getOnlyElement(rootNode.getChildren());
        assertNode(itemNode)
                .hasKlass(Foo.class)
                .hasTypeMappedTo(Foo.class, "X", "org.instancio.pojo.generics.foobarbaz." +
                        "Bar<org.instancio.pojo.generics.foobarbaz." +
                        "Baz<java.lang.String>>")
                .hasGenericTypeName("org.instancio.pojo.generics.foobarbaz." +
                        "Foo<org.instancio.pojo.generics.foobarbaz." +
                        "Bar<org.instancio.pojo.generics.foobarbaz." +
                        "Baz<java.lang.String>>>")
                .hasChildrenOfSize(2);

        final Node fooValueNode = NodeUtils.getChildNode(itemNode, "fooValue");
        assertNode(fooValueNode)
                .hasKlass(Bar.class)
                .hasTypeMappedTo(Bar.class, "Y", "org.instancio.pojo.generics.foobarbaz." +
                        "Baz<java.lang.String>")
                .hasGenericTypeName("org.instancio.pojo.generics.foobarbaz." +
                        "Bar<org.instancio.pojo.generics.foobarbaz." +
                        "Baz<java.lang.String>>")
                .hasChildrenOfSize(2);

        assertNode(NodeUtils.getChildNode(itemNode, "otherFooValue"))
                .hasKlass(Object.class)
                .hasNoChildren();

        final Node barValueNode = NodeUtils.getChildNode(fooValueNode, "barValue");
        assertNode(barValueNode)
                .hasKlass(Baz.class)
                .hasTypeMappedTo(Baz.class, "Z", String.class)
                .hasGenericTypeName("org.instancio.pojo.generics.foobarbaz.Baz<java.lang.String>")
                .hasChildrenOfSize(1);

        assertNode(NodeUtils.getChildNode(fooValueNode, "otherBarValue"))
                .hasKlass(Object.class)
                .hasNoChildren();

        assertNode(getOnlyElement(barValueNode.getChildren()))
                .hasKlass(String.class)
                .hasGenericTypeName("Z")
                .hasNoChildren();
    }
}