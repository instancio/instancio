package org.instancio.generation;

import org.instancio.Instancio;
import org.instancio.pojo.generics.MiscFields;
import org.instancio.pojo.generics.container.Pair;
import org.instancio.pojo.generics.container.Triplet;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Foo;
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

        assertThat(result.getFieldA()).isInstanceOf(UUID.class);

        assertThat(result.getPairAB()).isInstanceOf(Pair.class)
                .satisfies(pair -> {
                    assertThat(pair.getLeft()).isInstanceOf(UUID.class);
                    assertThat(pair.getRight()).isInstanceOf(String.class);
                });

        // FIXME looks like only first pair gets populated.. probably bug in cyclical reference check
        assertThat(result.getPairBA()).isInstanceOf(Pair.class)
                .satisfies(pair -> {
                    assertThat(pair.getLeft()).isInstanceOf(String.class);
                    assertThat(pair.getRight()).isInstanceOf(UUID.class);
                });

        assertThat(result.getPairLongPairIntegerString()).isInstanceOf(Pair.class);
        assertThat(result.getPairAPairIntegerString()).isInstanceOf(Pair.class);
        assertThat(result.getPairFooBarStringB()).isInstanceOf(Pair.class);

        assertThat(result.getTripletA_FooBarBazString_ListOfC()).isInstanceOf(Triplet.class);
        assertThat(result.getPairAFooBarB()).isInstanceOf(Pair.class);
        assertThat(result.getPairAString()).isInstanceOf(Pair.class);
        assertThat(result.getListOfCs()).isInstanceOf(List.class);
        assertThat(result.getListOfBazStrings()).isInstanceOf(List.class);
        assertThat(result.getArrayOfCs()).isInstanceOf(Long[].class);
        assertThat(result.getArrayOfInts()).isInstanceOf(int[].class);
        assertThat(result.getFooBarPairBC()).isInstanceOf(Foo.class);
        assertThat(result.getFooBarBazString()).isInstanceOf(Foo.class);
        assertThat(result.getListOfFoo_ListOfBar_ListOfBaz_ListOfString()).isInstanceOf(List.class);
    }

}
