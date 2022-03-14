package org.instancio.creation.generics;

import org.instancio.pojo.collections.IntegerStringMap;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class IntegerStringMapCreationTest extends CreationTestTemplate<IntegerStringMap> {

    @Override
    protected void verify(IntegerStringMap result) {
        assertThat(result.getMapField())
                .hasSize(Constants.COLLECTION_SIZE);

        assertThat(result.getMapField().entrySet())
                .allSatisfy(entry -> {
                    assertThat(entry.getKey()).isInstanceOf(Integer.class);
                    assertThat(entry.getValue()).isInstanceOf(String.class);
                });

    }

}
