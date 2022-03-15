package org.instancio.creation.generics;

import org.instancio.pojo.collections.StringPersonMap;
import org.instancio.pojo.person.Person;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class StringPersonMapCreationTest extends CreationTestTemplate<StringPersonMap> {

    @Override
    protected void verify(StringPersonMap result) {
        assertThat(result.getMapField())
                .hasSize(Constants.COLLECTION_SIZE);

        assertThat(result.getMapField().entrySet())
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
