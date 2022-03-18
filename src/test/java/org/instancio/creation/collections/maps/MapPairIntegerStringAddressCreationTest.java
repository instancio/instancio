package org.instancio.creation.collections.maps;

import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.person.Address;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class MapPairIntegerStringAddressCreationTest extends CreationTestTemplate<Map<Pair<Integer, String>, Address>> {

    @Override
    protected void verify(Map<Pair<Integer, String>, Address> result) {
        assertThat(result).hasSize(Constants.MAP_SIZE);
        final Set<Map.Entry<Pair<Integer, String>, Address>> entries = result.entrySet();

        assertThat(entries)
                .doesNotHaveDuplicates()
                .allSatisfy(entry -> {
                    assertThat(entry.getKey()).isInstanceOf(Pair.class);
                    assertThat(entry.getValue()).isInstanceOf(Address.class);
                });

    }
}
