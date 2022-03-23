package org.instancio.creation.generics;

import org.instancio.pojo.generics.MiscFields;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.foobarbaz.Bar;
import org.instancio.pojo.generics.foobarbaz.Baz;
import org.instancio.pojo.generics.foobarbaz.Foo;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class MiscFieldsCreationTest extends CreationTestTemplate<MiscFields<UUID, String, Long>> {

    @Override
    protected void verify(MiscFields<UUID, String, Long> result) {
        assertArrayOfCs(result);
        assertFieldA(result);
        assertFooBarBazString(result);
        assertFooBarPairBC(result);
        assertListOfBazStrings(result);
        assertListOfCs(result);
        assertPairAB(result);
        assertPairBA(result);
        assertPairFooBarStringB(result);
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

    private void assertArrayOfCs(MiscFields<?, ?, ?> result) {
        assertThat(result.getArrayOfCs()).isInstanceOf(Long[].class)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .hasOnlyElementsOfType(Long.class)
                .doesNotContainNull();
    }

    private void assertListOfBazStrings(MiscFields<?, ?, ?> result) {
        assertThat(result.getListOfBazStrings()).isInstanceOf(List.class)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .hasOnlyElementsOfType(Baz.class)
                .allSatisfy(baz ->
                        assertThat(baz.getBazValue()).isInstanceOf(String.class));
    }

    private void assertListOfCs(MiscFields<?, ?, ?> result) {
        assertThat(result.getListOfCs()).isInstanceOf(List.class)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
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
