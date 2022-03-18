package org.instancio.creation.array.misc;

import org.instancio.pojo.person.Address;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ArrayAddressTest extends CreationTestTemplate<Address[]> {

    @Override
    protected void verify(Address[] result) {
        assertThat(result)
                .hasSize(Constants.ARRAY_SIZE)
                .allSatisfy(element -> {
                    assertThat(element).isInstanceOf(Address.class);
                });
    }
}
