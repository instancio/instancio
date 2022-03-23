package org.instancio.creation.collections.maps;

import org.instancio.pojo.collections.maps.MapIntegerArrayString;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class MapIntegerArrayStringCreationTest extends CreationTestTemplate<MapIntegerArrayString> {

    @Override
    protected void verify(MapIntegerArrayString result) {
        final Map<Integer, String[]> map = result.getMap();
        assertThat(map).isNotEmpty();

        assertThat(map.entrySet()).allSatisfy(entry -> {
            assertThat(entry.getKey()).isInstanceOf(Integer.class);
            assertThat(entry.getValue())
                    .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                    .hasOnlyElementsOfType(String.class);
        });
    }

}
