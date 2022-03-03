package org.instancio;

import org.junit.jupiter.api.Test;
import org.instancio.exception.InstancioException;
import org.instancio.pojo.generics.FooContainer;
import org.instancio.pojo.generics.container.GenericContainer;
import org.instancio.pojo.generics.container.GenericItem;
import org.instancio.pojo.generics.container.GenericItemContainer;
import org.instancio.pojo.generics.container.GenericPair;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Bar;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Baz;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Foo;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.FooBarBazContainer;
import org.instancio.pojo.generics.outermidinner.ListOfOuterMidInnerString;
import org.instancio.pojo.person.Address;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InstancioGenericsTest {

    @Test
    void address() {
        Address result = Instancio.of(Address.class).create();

        assertThat(result.getPhoneNumbers()).isNotEmpty();

        result.getPhoneNumbers().forEach(phone -> {
            assertThat(phone.getCountryCode()).isNotBlank();
            assertThat(phone.getNumber()).isNotBlank();
        });
    }

    @Test
    void fooContainer() {
        FooContainer result = Instancio.of(FooContainer.class).create();

        System.out.println(result);
        assertThat(result).isNotNull();
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getFooValue()).isNotNull().isInstanceOf(String.class);
        assertThat(result.getItem().getOtherFooValue()).isNotNull();
    }

    @Test
    void fooContainerWithUserSuppliedInstance() {
        FooContainer result = Instancio.of(FooContainer.class)
                                       .with("item", () -> {
                    FooContainer.Foo<String> foo = new FooContainer.Foo<>();
                    foo.setFooValue("test");
                    return foo;
                })
                                       .create();

        assertThat(result).isNotNull();
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getFooValue()).isNotNull().isInstanceOf(String.class);
        assertThat(result.getItem().getOtherFooValue()).as("Value was not set").isNull();
    }

    // FIXME
    @Test
    void genericItemContainer() {
        // NOTE Unbound type variables, so this will not work...
        // Need a way to specify type variables, e.g.
        //
        // Instancio.of(GenericItemContainer.class)
        //       .withTypes(Integer.class, String.class)
        //       .create();
        //
        // If 'withTypes()' is omitted, should through an error saying
        // "unbound generic types. use `withTypes()` to specify the types"
        GenericItemContainer<Integer, String> container = Instancio.of(GenericItemContainer.class)
                                                                   .withType(Integer.class, String.class)
                                                                   .create();
        System.out.println(container);
    }

    @Test
    void genericItem() {
        GenericItem genericItem = Instancio.of(GenericItem.class)
                                           .withType(String.class)
                                           .create();

        System.out.println(genericItem.toString());
    }

    @Test
    void genericPair() {
        GenericPair<String, Integer> genericItem = Instancio.of(GenericPair.class)
                                                            .withType(String.class, Integer.class)
                                                            .create();

        System.out.println(genericItem.toString());
    }

    @Test
    void unboundTypeVariablesErrorMessage() {
        assertThatThrownBy(() -> Instancio.of(GenericItemContainer.class).create())
                .isInstanceOf(InstancioException.class)
                .hasMessage("Generic class %s " +
                        "has 2 type parameters: [L, R]. Please specify all type parameters using " +
                        "'withType(Class... types)`", GenericItemContainer.class.getName());

        assertThatThrownBy(() -> Instancio.of(List.class).create())
                .hasMessage("Generic class java.util.List has 1 type parameters: [E]." +
                        " Please specify all type parameters using 'withType(Class... types)`");
    }

    @Test
    void genericContainer() {
        GenericContainer<String> container = Instancio.of(GenericContainer.class)
                                                      .withType(String.class)
                                                      .create();

        assertThat(container.getValue()).isInstanceOf(String.class);
        assertThat(container.getList()).isNotEmpty().hasOnlyElementsOfType(String.class);
        assertThat(container.getArray()).isNotEmpty().hasOnlyElementsOfType(String.class);
    }

    @Test
    void fooBarBazContainer() {

        FooBarBazContainer result = Instancio.of(FooBarBazContainer.class).create();
        assertThat(result).isNotNull();

        System.out.println(result);

        final Foo<Bar<Baz<String>>> item = result.getItem();
        assertThat(item).isNotNull();
        assertThat(item.getOtherFooValue()).isExactlyInstanceOf(Object.class);

        final Bar<Baz<String>> fooValue = item.getFooValue();
        assertThat(fooValue).isExactlyInstanceOf(Bar.class);
        assertThat(fooValue.getOtherBarValue()).isExactlyInstanceOf(Object.class);

        final Baz<String> barValue = fooValue.getBarValue();
        assertThat(barValue).isExactlyInstanceOf(Baz.class);
        assertThat(barValue.getBazValue()).isInstanceOf(String.class);
    }

    @Test
    void listOfOuterMidInnerString() {
        ListOfOuterMidInnerString result = Instancio.of(ListOfOuterMidInnerString.class).create();
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isNotEmpty();

        System.out.println(result);

        result.getItems().forEach(outer -> {
            assertThat(outer).isNotNull();
            assertThat(outer.getOuterList()).isNotEmpty();

            outer.getOuterList().forEach(mid -> {
                assertThat(mid).isNotNull();
                assertThat(mid.getMidList()).isNotEmpty();
            });
        });

    }

}
