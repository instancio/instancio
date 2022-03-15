package org.instancio.creation.collections.maps;

import org.instancio.pojo.collections.maps.MapIntegerString;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class MapIntegerStringCreationTest extends CreationTestTemplate<MapIntegerString> {

    @Override
    protected void verify(MapIntegerString result) {
        assertThat(result.getMap())
                .hasSize(Constants.COLLECTION_SIZE);

        assertThat(result.getMap().entrySet())
                .allSatisfy(entry -> {
                    assertThat(entry.getKey()).isInstanceOf(Integer.class);
                    assertThat(entry.getValue()).isInstanceOf(String.class);
                });

    }

}
