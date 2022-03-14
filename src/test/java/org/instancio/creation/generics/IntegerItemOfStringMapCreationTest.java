package org.instancio.creation.generics;

import org.instancio.pojo.collections.IntegerItemOfStringMap;
import org.instancio.pojo.generics.basic.Item;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class IntegerItemOfStringMapCreationTest extends CreationTestTemplate<IntegerItemOfStringMap> {

    @Override
    protected void verify(IntegerItemOfStringMap result) {
        assertThat(result.getMapField())
                .hasSize(Constants.COLLECTION_SIZE);

        assertThat(result.getMapField().entrySet())
                .allSatisfy(entry -> {
                    assertThat(entry.getKey()).isInstanceOf(Integer.class);
                    assertThat(entry.getValue()).isInstanceOf(Item.class)
                            .satisfies(it -> assertThat(it.getValue()).isInstanceOf(String.class));
                });

    }

}
