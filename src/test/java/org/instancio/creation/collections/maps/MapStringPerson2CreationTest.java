package org.instancio.creation.collections.maps;

import org.instancio.pojo.collections.maps.MapStringPerson;
import org.instancio.pojo.person.Person;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class MapStringPerson2CreationTest extends CreationTestTemplate<MapStringPerson> {

    @Override
    protected void verify(MapStringPerson result) {
        assertThat(result.getMap())
                .hasSize(Constants.COLLECTION_SIZE);

        assertThat(result.getMap().entrySet())
                .allSatisfy(entry -> {
                    assertThat(entry.getKey()).isInstanceOf(String.class);
                    assertThat(entry.getValue()).isInstanceOf(Person.class)
                            .satisfies(person -> {
                                assertThat(person.getName()).isNotBlank();
                                assertThat(person.getAddress()).isNotNull();
                            });
                });

    }

}
