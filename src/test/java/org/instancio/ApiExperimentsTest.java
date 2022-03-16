package org.instancio;

import org.instancio.pojo.circular.HierarchyWithMultipleInterfaceImpls;
import org.instancio.pojo.inheritance.BaseClasSubClassInheritance;
import org.instancio.pojo.interfaces.MultipleInterfaceImpls;
import org.instancio.pojo.interfaces.SingleInterfaceImpl;
import org.instancio.pojo.person.Person;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Generators.oneOf;

@Disabled
@SuppressWarnings({"java:S1607", "java:S2699"})
class ApiExperimentsTest {

    @Test
    void inheritanceChildClass() {
        BaseClasSubClassInheritance.SubClass result = Instancio.of(BaseClasSubClassInheritance.SubClass.class).create();

        System.out.println(result);

        assertThat(result.getSubClassField()).isNotBlank();
        assertThat(result.getProtectedBaseClassField()).isNotBlank();
        assertThat(result.getPrivateBaseClassField()).isNotBlank();
    }

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
    void generatorArgIsConfusing() {
        // TODO this is creating a generator that returns another generator...
        //  find a way to deal with this.
        Person person = Instancio.of(Person.class)
                .with("name", () -> oneOf("1", "2", "3"))
                .create();
    }

    @Test
    void invalidCustomFieldGenerator() {
        assertThatThrownBy(() -> Instancio.of(Person.class).with("name", () -> 123).create())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Can not set java.lang.String field org.instancio.pojo.person.Person.name to java.lang.Integer");
    }


    @Test
    @Disabled
    void createMapDirectly() {
        Map<Integer, String> result = Instancio.of(Map.class)
                .withType(Integer.class, String.class)
                .create();

        assertThat(result).isNotEmpty();
    }

    @Test
    @Disabled
    void createListDirectly() {
        List<Integer> result = Instancio.of(List.class)
                .withType(Integer.class)
                //.withSize(5)
                .create();

        assertThat(result).isNotEmpty();
    }

    @Test
    @Disabled
    void apiExperiments() {

        // Domain settings
//        Instancio.of(Person.class)
//                .withDomain(personDomainSettings())
//                .create();

        // (?) might not be worth to deal with arrays
//        Person[] personArray = Instancio.ofArray(Person.class/*, size(5)*/)
//                .with("someInt", () -> 12345)
//                .create();


//        List<Person> personList = Instancio.ofList(Person.class).create();

/*
        Set<Person> personSet = Instancio.ofSet(Person.class, size(5)).create();
        Map<Integer, Person> personMap= Instancio.ofMap(Integer.class, Person.class, size(5)).create();
*/

        // Alternative way to specify fields is to use `field(Foo.clas, "fooField")` syntax.
        // This will remove the need to parse field paths like "address.phoneNumbers(?).number" (where phoneNumbers is a List)
//        Instancio.of(Person.class)
//                .ignore(field(Address.class, "city"))
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
        ObjectCreationSettingsAPI<Person, ?> settings = Instancio.of(Person.class).ignore("address.city");
        Person p = settings.create();
        // ===========

        Instancio.of(Person.class)
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
