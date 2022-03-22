package org.instancio.creation.generics;

import org.instancio.pojo.generics.TripletAFooBarBazStringListOfB;
import org.instancio.pojo.generics.basic.Triplet;
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
public class TripletAFooBarBazStringListOfBCreationTest extends CreationTestTemplate<TripletAFooBarBazStringListOfB<UUID, Long>> {

    @Override
    protected void verify(TripletAFooBarBazStringListOfB<UUID, Long> result) {
        final Triplet<?, Foo<Bar<Baz<String>>>, ? extends List<?>> triplet = result.getTripletA_FooBarBazString_ListOfB();
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
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .hasOnlyElementsOfType(Long.class);
    }
}
