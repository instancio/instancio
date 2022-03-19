package org.instancio.creation.collections.maps;

import org.instancio.pojo.person.Person;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class MapStringPerson1CreationTest extends CreationTestTemplate<Map<String, Person>> {

    @Override
    protected void verify(Map<String, Person> result) {
        assertThat(result).hasSize(Constants.COLLECTION_SIZE);

        assertThat(result.entrySet())
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
