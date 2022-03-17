package org.instancio.api;

import lombok.Getter;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.TypeToken;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.basic.Triplet;
import org.instancio.pojo.person.Address;
import org.instancio.pojo.person.Gender;
import org.instancio.pojo.person.Person;
import org.instancio.pojo.person.Pet;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Smoke test invoking various API methods.
 */
class InstancioApiTest {

    private static final String HOMER = "Homer";
    private static final int HOMER_AGE = 40;
    private static final String SPRINGFIELD = "Springfield";

    @Getter
    static class AddressExtension extends Address {
        private String additionalInfo;
    }

    @Test
    void createFromClass() {
        final Person homer = Instancio.of(Person.class)
                .with("name", () -> HOMER)
                .with(Person.class, "age", () -> HOMER_AGE)
                .with(LocalDateTime.class, LocalDateTime::now)
                .withNullable("date")
                .withNullable(Person.class, "pets")
                .withNullable(Gender.class)
                .map(Address.class, AddressExtension.class)
                .create();

        assertThat(homer.getName()).isEqualTo(HOMER);
        assertThat(homer.getAge()).isEqualTo(HOMER_AGE);
        assertThat(homer.getLastModified()).isCloseTo(LocalDateTime.now(), within(3, ChronoUnit.SECONDS));
        // TODO to implement...
//        assertThat(homer.getAddress())
//                .isExactlyInstanceOf(AddressExtension.class)
//                .satisfies(it -> assertThat(((AddressExtension) it).getAdditionalInfo()).isNotBlank());
    }

    @Test
    void createFromClassWithTypeParameters() {
        //noinspection unchecked
        final Pair<Integer, String> pair = Instancio.of(Pair.class)
                .withTypeParameters(Integer.class, String.class)
                .create();

        assertThat(pair.getLeft()).isInstanceOf(Integer.class);
        assertThat(pair.getRight()).isInstanceOf(String.class);
    }

    @Test
    void createFromType() {
        final Pair<Integer, Triplet<String, Long, Boolean>> pair = Instancio.of(
                new TypeToken<Pair<Integer, Triplet<String, Long, Boolean>>>() {
                }).create();

        assertThat(pair.getLeft()).isInstanceOf(Integer.class);
        assertThat(pair.getRight()).isInstanceOf(Triplet.class);

        final Triplet<String, Long, Boolean> triplet = pair.getRight();
        assertThat(triplet.getLeft()).isInstanceOf(String.class);
        assertThat(triplet.getMid()).isInstanceOf(Long.class);
        assertThat(triplet.getRight()).isInstanceOf(Boolean.class);
    }

    @Test
    void createFromModel() {
        final Model<Person> homerModel = Instancio.of(Person.class)
                .with("name", () -> HOMER)
                .with(Person.class, "age", () -> HOMER_AGE)
                .with(LocalDateTime.class, LocalDateTime::now)
                .with(Address.class, "city", () -> SPRINGFIELD)
                .ignore(Person.class, "date")
                .ignore(Gender.class)
                .toModel();

        final Person homer = Instancio.of(homerModel).create();

        assertThat(homer.getName()).isEqualTo(HOMER);
        assertThat(homer.getAge()).isEqualTo(HOMER_AGE);
        assertThat(homer.getLastModified()).isCloseTo(LocalDateTime.now(), within(3, ChronoUnit.SECONDS));
        assertThat(homer.getAddress().getCity()).isEqualTo(SPRINGFIELD);
        assertThat(homer.getDate()).isNull();
        assertThat(homer.getGender()).isNull();
        assertThat(homer.getPets())
                .isNotEmpty()
                .hasOnlyElementsOfType(Pet.class);
    }
}
