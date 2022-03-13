package org.instancio.creation.generics;

import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.foobarbaz.Bar;
import org.instancio.pojo.generics.foobarbaz.Foo;
import org.instancio.pojo.generics.foobarbaz.PairAFooBarB;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class PairAFooBarBCreationTest extends CreationTestTemplate<PairAFooBarB<UUID, String>> {

    @Override
    protected void verify(PairAFooBarB<UUID, String> result) {
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

}
