package org.instancio.creation.collections.maps;

import org.instancio.pojo.collections.maps.MapIntegerListString;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class MapIntegerListString1CreationTest extends CreationTestTemplate<MapIntegerListString> {

    @Override
    protected void verify(MapIntegerListString result) {
        final Map<Integer, List<String>> map = result.getMap();
        assertThat(map).hasSize(Constants.MAP_SIZE);

        assertThat(map.entrySet()).allSatisfy(entry -> {
            assertThat(entry.getKey()).isInstanceOf(Integer.class);
            assertThat(entry.getValue())
                    .hasSize(Constants.COLLECTION_SIZE)
                    .hasOnlyElementsOfType(String.class);
        });
    }

}
