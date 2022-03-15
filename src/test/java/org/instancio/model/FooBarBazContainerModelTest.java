package org.instancio.model;

import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.container.PairContainer;
import org.instancio.pojo.generics.foobarbaz.FooBarBazContainer;
import org.instancio.testsupport.templates.ModelTestTemplate;
import org.instancio.testsupport.utils.NodeUtils;

import static org.assertj.core.api.Fail.fail;
import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

class FooBarBazContainerModelTest extends ModelTestTemplate<FooBarBazContainer> {

    @Override
    protected void verify(Node rootNode) {
        assertNode(rootNode)
                .hasKlass(FooBarBazContainer.class)
                .hasChildrenOfSize(1);

        fail("TODO");
   }
}