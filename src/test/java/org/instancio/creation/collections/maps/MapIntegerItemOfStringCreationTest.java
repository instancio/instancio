package org.instancio.creation.collections.maps;

import org.instancio.pojo.collections.maps.MapIntegerItemOfString;
import org.instancio.pojo.generics.basic.Item;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class MapIntegerItemOfStringCreationTest extends CreationTestTemplate<MapIntegerItemOfString> {

    @Override
    protected void verify(MapIntegerItemOfString result) {
        assertThat(result.getMap())
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

        assertThat(result.getMap().entrySet())
                .allSatisfy(entry -> {
                    assertThat(entry.getKey()).isInstanceOf(Integer.class);
                    assertThat(entry.getValue()).isInstanceOf(Item.class)
                            .satisfies(it -> assertThat(it.getValue()).isInstanceOf(String.class));
                });

    }

}
