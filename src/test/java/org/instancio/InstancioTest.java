package org.instancio;

import org.instancio.pojo.circular.HierarchyWithMultipleInterfaceImpls;
import org.instancio.pojo.collections.IntegerStringMap;
import org.instancio.pojo.collections.StringPersonMap;
import org.instancio.pojo.collections.WithMiscMapInterfaces;
import org.instancio.pojo.generics.container.GenericContainer;
import org.instancio.pojo.generics.container.ItemContainer;
import org.instancio.pojo.generics.foobarbaz.Foo;
import org.instancio.pojo.generics.foobarbaz.FooContainer;
import org.instancio.pojo.interfaces.MultipleInterfaceImpls;
import org.instancio.pojo.interfaces.SingleInterfaceImpl;
import org.instancio.pojo.person.Gender;
import org.instancio.pojo.person.Person;
import org.instancio.pojo.person.Pet;
import org.instancio.pojo.person.PopulatedPerson;
import org.instancio.util.Random;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.instancio.Generators.nullValue;
import static org.instancio.Generators.oneOf;
import static org.instancio.Generators.withPrefix;

class InstancioTest {

    @Test
    void singleInterfaceImpl() {
        SingleInterfaceImpl.WidgetContainer widgetContainer = Instancio.of(SingleInterfaceImpl.WidgetContainer.class).create();

        assertThat(widgetContainer).isNotNull();
        assertThat(widgetContainer.getWidget()).isNotNull();
        assertThat(widgetContainer.getWidget().getWidgetName()).isNotNull();
    }

    @Test
    void multipleInterfaceImpls() {
        MultipleInterfaceImpls.WidgetContainer widgetContainer = Instancio.of(MultipleInterfaceImpls.WidgetContainer.class).create();

        assertThat(widgetContainer).isNotNull();
        assertThat(widgetContainer.getWidget()).isNull();
    }

    @Test
    void hierarchyWithMultipleInterfaceImpls() {
        Instancio.of(HierarchyWithMultipleInterfaceImpls.A.class).create();
    }


    @Test
    void smokeTest() {
        Person person = Instancio.of(Person.class).create();

        System.out.println(person);

        assertThat(person).isNotNull();
        assertThat(person.getName()).isNotBlank();
        assertThat(person.getAddress()).isNotNull();
        assertThat(person.getAddress().getPhoneNumbers()).isNotEmpty();
        assertThat(person.getAddress().getCity()).isNotNull();
        assertThat(Person.getStaticField()).isNull();
    }

    @Test
    void generatePersonWithNullAndNullable() {
        Person person = Instancio.of(Person.class)
                .withNullable("name")
  //              .with("address.city", nullValue())
                .with("pets", nullValue())
                .create();

        System.out.println("Person name " + person.getName());

//        assertThat(person.getAddress().getCity()).isNull();
        assertThat(person.getPets()).isNull();

        //assertThat(person.getName()).isNotBlank();
    }

    @Test
    void exclusions() {
        PopulatedPerson expected = new PopulatedPerson();
        PopulatedPerson actual = Instancio.of(PopulatedPerson.class)
                .exclude("person", "someString", "someInt")
                .create();

        assertThat(actual.getPerson())
                .as("Should retain pre-initialized values")
                .isEqualTo(expected.getPerson());
    }

    @Test
    void generatePersonWithExclusions() {
        Person person = Instancio.of(Person.class)
                .exclude("gender", "lastModified", "address.city")
                //.exclude("address.phone.countryCode") // TODO broken - figure out what paths should look like for nested objects
                //.exclude("pet.name") // TODO broken
                .create();

        assertThat(person).isNotNull();
        assertThat(person.getLastModified()).isNull();
        assertThat(person.getAddress()).isNotNull();
        assertThat(person.getAddress().getCity()).isNull();
        //assertThat(person.getAddress())..
        //assertThat(person.getPet())..
    }

    @Test
    @Disabled
    void generatorArgIsConfusing() {
        // TODO this is creating a generator that returns another generator...
        //  find a way to deal with this. does it make sense to use generators
        //  in a decorator fashion?
        Person person = Instancio.of(Person.class)
                .with("name", () -> oneOf("1", "2", "3"))
                .create();
    }

    @Test
    void generatePersonWithCustomFieldGenerators() {
        String[] names = {"Jane", "John", "Bob"};

        Person person = Instancio.of(Person.class)
                .with("name", oneOf(names))
                .with("gender", () -> Gender.FEMALE)
                .with("lastModified", () -> LocalDateTime.now(ZoneOffset.UTC))
                .with("address.city", () -> "city-" + Random.intBetween(100, 999))
                //                .with("address.phone.countryCode", () -> "+1") // TODO broken
                //                .with("pet.name", () -> "Fido")
                .create();

        assertThat(person).isNotNull();
        assertThat(person.getName()).isIn(names);
        assertThat(person.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(person.getLastModified()).isCloseToUtcNow(within(3, ChronoUnit.SECONDS));
        assertThat(person.getAddress().getCity()).matches("city-\\d{3}");
        //assertThat(person.getAddress().getPhoneNumbers()) ...
        //assertThat(person.getPets() ...
    }

    @Test
    void invalidCustomFieldGenerator() {
        assertThatThrownBy(() -> Instancio.of(Person.class).with("name", () -> 123).create())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Can not set java.lang.String field org.instancio.pojo.person.Person.name to java.lang.Integer");
    }

    @Test
    void generateIntegerAndPrimitiveInt() {
        assertThat(Instancio.of(Integer.class).create()).isNotNull();
        assertThat(Instancio.of(int.class).create()).isNotNull();
    }

    @Test
    void generateString() {
        // TODO add test case for each core type
        assertThat(Instancio.of(Integer.class).create()).isNotNull();
        assertThat(Instancio.of(String.class).create()).isNotNull();
    }

    @Test
    void integerStringMap() {
        IntegerStringMap result = Instancio.of(IntegerStringMap.class).create();
        assertThat(result.getMapField()).isNotEmpty().hasSize(2);
        assertThat(result.getMapField().entrySet()).doesNotContainNull();
    }

    // FIXME
    @Test
    void withMiscMapInterfaces() {
        WithMiscMapInterfaces result = Instancio.of(WithMiscMapInterfaces.class).create();

        assertThat(result.getSortedMap()).isNotEmpty().hasSize(2);
        assertThat(result.getNavigableMap().entrySet()).doesNotContainNull();
        assertThat(result.getConcurrentNavigableMap().entrySet()).doesNotContainNull();
    }

    @Test
    void stringPersonMap() {
        StringPersonMap result = Instancio.of(StringPersonMap.class).create();
        assertThat(result.getMapField()).isNotEmpty().hasSize(2);
        assertThat(result.getMapField().entrySet()).doesNotContainNull();
        System.out.println(result);
    }

    @Test
    void stringFieldGeneratorWithCustomFormat() {
        Person person = Instancio.of(Person.class)
                .with("name", withPrefix("name-"))
                .with("age", oneOf(18, 28, 38, 48))
                .create();

        System.out.println(person);
        assertThat(person.getName()).isNotBlank();
    }

    @Test
    void stringClassGeneratorWithCustomFormat() {
        final String prefix = "custom-prefix-for-ALL-String-fields-";
        final String expectedCity = "city-value";

        // All String fields should use custom prefix except Address.city
        Person person = Instancio.of(Person.class)
                .with("address.city", () -> expectedCity)
                .with(String.class, withPrefix(prefix))
                .create();

        assertThat(person.getAddress().getCity()).isEqualTo(expectedCity);
        assertThat(person.getName()).startsWith(prefix);
        assertThat(person.getAddress().getAddress()).startsWith(prefix);
        assertThat(person.getAddress().getCountry()).startsWith(prefix);

        // FIXME failing due to array generation code needing refactoring
        person.getAddress().getPhoneNumbers().forEach(phone -> {
            assertThat(phone.getCountryCode()).startsWith(prefix);
            assertThat(phone.getNumber()).startsWith(prefix);
        });

        for (Pet pet : person.getPets()) {
            assertThat(pet.getName()).startsWith(prefix);
        }
    }

    @Disabled
    @Test
    void api() {
        // Generics
        Instancio.of(GenericContainer.class)
                //.withGenericType(String.class)
                .create();

        Instancio.of(ItemContainer.class)
                //.withGenericType(Integer.class, String.class)
                .create();

        // Domain settings
//        Instancio.of(Person.class)
//                .withDomain(personDomainSettings())
//                .create();

        Instancio.of(FooContainer.class)
                .with("item", () -> {
                    Foo<String> foo = new Foo<>();
                    foo.setFooValue("test");
                    return foo;
                })
                .create();

        // (?) might not be worth to deal with arrays
//        Person[] personArray = Instancio.ofArray(Person.class/*, size(5)*/)
//                .with("someInt", () -> 12345)
//                .create();


        List<Person> personList = Instancio.ofList(Person.class).create();

/*
        Set<Person> personSet = Instancio.ofSet(Person.class, size(5)).create();
        Map<Integer, Person> personMap= Instancio.ofMap(Integer.class, Person.class, size(5)).create();
*/

        // Alternative way to specify fields is to use `field(Foo.clas, "fooField")` syntax.
        // This will remove the need to parse field paths like "address.phoneNumbers(?).number" (where phoneNumbers is a List)
//        Instancio.of(Person.class)
//                .exclude("gender")
//                .exclude(field(Address.class, "city"))
//                .with(field(Phone.class, "number"))
//                .create();


        // TODO clean up awkward API.. return an instance of Instancio instead? e.g.
        //  Instancio<> instancio = Instancio.of(Person.class);
        //  Person p = instancio.create();
        //  //Array
        //  (option 1) ArrayInstancio<> instancio = Instancio.ofArray(Person.class);
        //  (option 2) Instancio<> instancio = Instancio.ofArray(Person.class);
        //  Person[] p = instancio.create();
        //
        ObjectCreationSettingsAPI<Person, ?> settings = Instancio.of(Person.class).exclude("address.city");
        Person p = settings.create();
        // ===========

        Instancio.of(Person.class)
                .exclude("gender")
                .exclude("address.city")
                .with("country", () -> "some other value")
                .with("someInt", () -> 12345)
                //                .with("age", min(18))
                //.with("someInt", between(100,999))
                //                .with("phoneNumbers", size(5))
                //                .with("phone.countryCode", "+1")
                //                .with("pets.name", withPrefix("pet-name-"))
                //
                // ParentSetter / Bi-directional associations
                //.with("pet.owner", root())
                //
                // Family->FamilyMember[]->Vehicle(->familyMember)
                //.with("vehicle.owner", parent("familyMember"))
                //
                .create();
    }
}
