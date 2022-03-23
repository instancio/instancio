package org.instancio.api.features.generators;

import org.instancio.Instancio;
import org.instancio.pojo.collections.lists.TwoListsOfItemString;
import org.instancio.pojo.person.Address;
import org.instancio.pojo.person.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.allStrings;
import static org.instancio.Bindings.field;

class BuiltInStringGeneratorTest {

    private static final int MIN_LENGTH = 50;
    private static final String PREFIX = "PREFIX-";

    @Test
    @DisplayName("All strings should start with prefix: when strings are collection elements")
    void stringPrefixForAllStringsInsideCollection() {
        final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                .generate(allStrings(), gen -> gen.string().minLength(MIN_LENGTH).prefix(PREFIX))
                .create();

        assertThat(result.getList1()).isNotEmpty()
                .allSatisfy(item -> assertThat(item.getValue())
                        .startsWith(PREFIX)
                        .hasSizeGreaterThanOrEqualTo(MIN_LENGTH + PREFIX.length()));

        assertThat(result.getList2()).isNotEmpty()
                .allSatisfy(item -> assertThat(item.getValue())
                        .startsWith(PREFIX)
                        .hasSizeGreaterThanOrEqualTo(MIN_LENGTH + PREFIX.length()));
    }

    @Test
    @DisplayName("All strings should be random values with a prefix except certain fields")
    void stringPrefixForAllStringsExceptCertainFields() {
        final String homer = "Homer";
        final String springfield = "Springfield";
        final Person person = Instancio.of(Person.class)
                .supply(field("name"), () -> homer)
                .supply(field(Address.class, "city"), () -> springfield)
                .generate(allStrings(), gen -> gen.string().prefix(PREFIX))
                .create();

        assertThat(person.getName()).isEqualTo(homer);
        assertThat(person.getAddress().getCity()).isEqualTo(springfield);
        assertThat(person.getAddress().getCountry()).startsWith(PREFIX);
    }
}
