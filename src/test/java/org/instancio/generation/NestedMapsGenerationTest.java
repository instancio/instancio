package org.instancio.generation;

import org.instancio.Instancio;
import org.instancio.pojo.generics.NestedMaps;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.CreateTag;
import org.instancio.testsupport.tags.GenericsTag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@CreateTag
@GenericsTag
public class NestedMapsGenerationTest {

    @Test
    void generate() {
        NestedMaps<?, ?> result = Instancio.of(NestedMaps.class)
                .withType(String.class, Integer.class)
                .create();

        System.out.println(result);
        assertThat(result).isNotNull();

        // FIXME nested map is not being populated due to missing children
        assertMap1(result.getMap1());
        assertMap2(result.getMap2());
    }

    private void assertMap1(Map<Long, Map<String, Boolean>> map1) {
        assertThat(map1).hasSize(Constants.MAP_SIZE);
        assertThat(map1.keySet())
                .hasOnlyElementsOfType(Long.class)
                .doesNotContainNull();

        assertThat(map1.values())
                .hasOnlyElementsOfType(Map.class)
                .doesNotContainNull()
                .allSatisfy(nestedMap -> {

                    assertThat(nestedMap).hasSize(Constants.MAP_SIZE);

                    assertThat(nestedMap.keySet())
                            .hasOnlyElementsOfType(String.class)
                            .doesNotContainNull();

                    assertThat(nestedMap.values())
                            .hasOnlyElementsOfType(Boolean.class)
                            .doesNotContainNull();
                });
    }

    private void assertMap2(Map<?, ? extends Map<?, Boolean>> map1) {
        // Map<OKEY, Map<IKEY, Boolean>> map2

        assertThat(map1).hasSize(Constants.MAP_SIZE);
        assertThat(map1.keySet())
                .hasOnlyElementsOfType(String.class)
                .doesNotContainNull();

        assertThat(map1.values())
                .hasOnlyElementsOfType(Map.class)
                .doesNotContainNull()
                .allSatisfy(nestedMap -> {

                    assertThat(nestedMap).hasSize(Constants.MAP_SIZE);

                    assertThat(nestedMap.keySet())
                            .hasOnlyElementsOfType(Integer.class)
                            .doesNotContainNull();

                    assertThat(nestedMap.values())
                            .hasOnlyElementsOfType(Boolean.class)
                            .doesNotContainNull();
                });
    }
}
