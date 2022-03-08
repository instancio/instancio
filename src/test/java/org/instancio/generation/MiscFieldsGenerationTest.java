package org.instancio.generation;

import org.instancio.Instancio;
import org.instancio.pojo.generics.MiscFields;
import org.instancio.pojo.generics.container.Pair;
import org.instancio.pojo.generics.container.Triplet;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Bar;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Baz;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Foo;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.CreateTag;
import org.instancio.testsupport.tags.GenericsTag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@CreateTag
@GenericsTag
public class MiscFieldsGenerationTest {

    @Test
    void generate() {
        final MiscFields<?, ?, ?> result = Instancio.of(MiscFields.class)
                .withType(UUID.class, String.class, Long.class)
                .create();

        System.out.println(result);

        assertArrayOfCs(result);
        assertArrayOfInts(result);
        assertFieldA(result);
        assertFooBarBazString(result);
        assertFooBarPairBC(result);
        assertListOfBazStrings(result);
        assertListOfCs(result);
        assertListOfFoo_ListOfBar_ListOfBaz_ListOfString(result);
        assertPairAB(result);
        assertPairAFooBarB(result);
        assertPairAPairIntegerString(result);
        assertPairAString(result);
        assertPairBA(result);
        assertPairFooBarStringB(result);
        assertPairLongPairIntegerString(result);
        assertTripletA_FooBarBazString_ListOfC(result);
    }

    private void assertListOfFoo_ListOfBar_ListOfBaz_ListOfString(MiscFields<?, ?, ?> result) {
        // List<Foo<List<Bar<List<Baz<List<String>>>>>>> listOfFoo_ListOfBar_ListOfBaz_ListOfString

        final List<Foo<List<Bar<List<Baz<List<String>>>>>>> list = result.getListOfFoo_ListOfBar_ListOfBaz_ListOfString();
        assertThat(list)
                // List<Foo>
                .isInstanceOf(List.class)
                .hasSize(Constants.COLLECTION_SIZE)
                .hasOnlyElementsOfType(Foo.class)
                .allSatisfy(foo -> assertThat(foo.getFooValue())
                        // List<Bar>
                        .isInstanceOf(List.class)
                        .hasSize(Constants.COLLECTION_SIZE)
                        .hasOnlyElementsOfType(Bar.class)
                        .allSatisfy(bar -> assertThat(bar.getBarValue())
                                // List<Baz>
                                .isInstanceOf(List.class)
                                .hasSize(Constants.COLLECTION_SIZE)
                                .hasOnlyElementsOfType(Baz.class)
                                .allSatisfy(str -> assertThat(str)
                                        .isInstanceOf(String.class))));
    }

    private void assertFooBarBazString(MiscFields<?, ?, ?> result) {
        //Foo<Bar<Baz<String>>> fooBarBazString

        assertThat(result.getFooBarBazString()).isInstanceOf(Foo.class);
        assertThat(result.getFooBarBazString().getFooValue()).isInstanceOf(Bar.class);
        assertThat(result.getFooBarBazString().getOtherFooValue()).isExactlyInstanceOf(Object.class);
        assertThat(result.getFooBarBazString().getFooValue().getBarValue()).isInstanceOf(Baz.class);
        assertThat(result.getFooBarBazString().getFooValue().getOtherBarValue()).isExactlyInstanceOf(Object.class);
        assertThat(result.getFooBarBazString().getFooValue().getBarValue().getBazValue()).isInstanceOf(String.class);
    }

    private void assertFooBarPairBC(MiscFields<?, ?, ?> result) {
        // Foo<Bar<Pair<B, C>>> fooBarPairBC

        assertThat(result.getFooBarPairBC()).isInstanceOf(Foo.class);
        assertThat(result.getFooBarPairBC().getFooValue()).isInstanceOf(Bar.class);
        assertThat(result.getFooBarPairBC().getFooValue().getBarValue()).isInstanceOf(Pair.class)
                .satisfies(pair -> {
                    assertThat(pair.getLeft()).isInstanceOf(String.class);
                    assertThat(pair.getRight()).isInstanceOf(Long.class);
                });
    }

    private void assertArrayOfInts(MiscFields<?, ?, ?> result) {
        assertThat(result.getArrayOfInts()).isInstanceOf(int[].class)
                .hasSize(Constants.ARRAY_SIZE);
    }

    private void assertArrayOfCs(MiscFields<?, ?, ?> result) {
        assertThat(result.getArrayOfCs()).isInstanceOf(Long[].class)
                .hasSize(Constants.ARRAY_SIZE)
                .hasOnlyElementsOfType(Long.class)
                .doesNotContainNull();
    }

    private void assertListOfBazStrings(MiscFields<?, ?, ?> result) {
        assertThat(result.getListOfBazStrings()).isInstanceOf(List.class)
                .hasSize(Constants.COLLECTION_SIZE)
                .hasOnlyElementsOfType(Baz.class)
                .allSatisfy(baz ->
                        assertThat(baz.getBazValue()).isInstanceOf(String.class));
    }

    private void assertListOfCs(MiscFields<?, ?, ?> result) {
        assertThat(result.getListOfCs()).isInstanceOf(List.class)
                .hasSize(Constants.COLLECTION_SIZE)
                .hasOnlyElementsOfType(Long.class);
    }

    private void assertPairAString(MiscFields<?, ?, ?> result) {
        assertThat(result.getPairAString()).isInstanceOf(Pair.class)
                .satisfies(pair -> {
                    assertThat(pair.getLeft()).isInstanceOf(UUID.class);
                    assertThat(pair.getRight()).isInstanceOf(String.class);
                });
    }

    private void assertPairAFooBarB(MiscFields<?, ?, ?> result) {
        // Pair<A, Foo<Bar<B>>> pairAFooBarB

        assertThat(result.getPairAFooBarB()).isInstanceOf(Pair.class);
        assertThat(result.getPairAFooBarB().getLeft()).isInstanceOf(UUID.class);
        assertThat(result.getPairAFooBarB().getRight()).isInstanceOf(Foo.class)
                .satisfies(foo -> {
                    assertThat(foo.getOtherFooValue()).isExactlyInstanceOf(Object.class);
                    assertThat(foo.getFooValue()).isInstanceOf(Bar.class)
                            .satisfies(bar -> {
                                assertThat(bar.getOtherBarValue()).isExactlyInstanceOf(Object.class);
                                assertThat(bar.getBarValue()).isInstanceOf(String.class);
                            });
                });
    }

    private void assertTripletA_FooBarBazString_ListOfC(MiscFields<?, ?, ?> result) {
        //Triplet<A, Foo<Bar<Baz<String>>>, List<C>> tripletA_FooBarBazString_ListOfC

        final Triplet<?, Foo<Bar<Baz<String>>>, ? extends List<?>> triplet = result.getTripletA_FooBarBazString_ListOfC();
        assertThat(triplet).isInstanceOf(Triplet.class);

        assertThat(triplet.getLeft()).isInstanceOf(UUID.class);

        assertThat(triplet.getMid()).isInstanceOf(Foo.class).satisfies(foo -> {
            assertThat(foo.getOtherFooValue()).isExactlyInstanceOf(Object.class);
            assertThat(foo.getFooValue()).isInstanceOf(Bar.class)
                    .satisfies(bar -> {
                        assertThat(bar.getOtherBarValue()).isExactlyInstanceOf(Object.class);
                        assertThat(bar.getBarValue()).isInstanceOf(Baz.class)
                                .satisfies(baz -> assertThat(baz.getBazValue()).isInstanceOf(String.class).isNotBlank());
                    });
        });

        assertThat(triplet.getRight()).isInstanceOf(List.class)
                .hasSize(Constants.COLLECTION_SIZE)
                .hasOnlyElementsOfType(Long.class);
    }

    private void assertPairFooBarStringB(MiscFields<?, ?, ?> result) {
        // Pair<Foo<Bar<String>>, B> pairFooBarStringB

        assertThat(result.getPairFooBarStringB()).isInstanceOf(Pair.class);

        final Foo<Bar<String>> left = result.getPairFooBarStringB().getLeft();
        assertThat(left.getFooValue()).isNotNull();
        assertThat(left.getOtherFooValue()).isNotNull();
        assertThat(left.getFooValue().getBarValue()).isInstanceOf(String.class);
        assertThat(left.getFooValue().getOtherBarValue()).isNotNull();

        assertThat(result.getPairFooBarStringB().getRight()).isInstanceOf(String.class);
    }

    private void assertPairAPairIntegerString(MiscFields<?, ?, ?> result) {
        assertThat(result.getPairAPairIntegerString()).isInstanceOf(Pair.class).satisfies(pair -> {
            assertThat(pair.getLeft()).isInstanceOf(UUID.class);
            assertThat(pair.getRight()).isInstanceOf(Pair.class).satisfies(nestedPair -> {
                assertThat(nestedPair.getLeft()).isInstanceOf(Integer.class);
                assertThat(nestedPair.getRight()).isInstanceOf(String.class);
            });
        });
    }

    private void assertPairLongPairIntegerString(MiscFields<?, ?, ?> result) {
        assertThat(result.getPairLongPairIntegerString()).isInstanceOf(Pair.class);
        assertThat(result.getPairLongPairIntegerString().getLeft()).isInstanceOf(Long.class);

        final Pair<Integer, String> nestedPair = result.getPairLongPairIntegerString().getRight();

        assertThat(nestedPair).satisfies(pair -> {
            assertThat(pair.getLeft()).isInstanceOf(Integer.class);
            assertThat(pair.getRight()).isInstanceOf(String.class);
        });
    }

    private void assertPairBA(MiscFields<?, ?, ?> result) {
        assertThat(result.getPairBA()).isInstanceOf(Pair.class)
                .satisfies(pair -> {
                    assertThat(pair.getLeft()).isInstanceOf(String.class);
                    assertThat(pair.getRight()).isInstanceOf(UUID.class);
                });
    }

    private void assertFieldA(MiscFields<?, ?, ?> result) {
        assertThat(result.getFieldA()).isInstanceOf(UUID.class);
    }

    private void assertPairAB(MiscFields<?, ?, ?> result) {
        assertThat(result.getPairAB()).isInstanceOf(Pair.class)
                .satisfies(pair -> {
                    assertThat(pair.getLeft()).isInstanceOf(UUID.class);
                    assertThat(pair.getRight()).isInstanceOf(String.class);
                });
    }

}
