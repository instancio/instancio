package org.instancio.model;

import org.instancio.testsupport.templates.ModelTestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

public class ListLocalDateTimeModelTest extends ModelTestTemplate<List<LocalDateTime>> {

    @Override
    protected void verify(final Node rootNode) {
        final CollectionNode listNode = assertNode(rootNode)
                .hasKlass(List.class)
                .hasTypeMappedTo(List.class, "E", LocalDateTime.class)
                .hasNoChildren()
                .getAs(CollectionNode.class);

        assertNode(listNode.getElementNode())
                .hasParent(listNode)
                .hasNullField()
                .hasKlass(LocalDateTime.class)
                .hasGenericType(null)
                .hasEmptyTypeMap();
    }
}
