package org.instancio.creation.generics;

import org.instancio.pojo.generics.basic.Item;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.basic.Triplet;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class NestedPairTripletMapPairItemCreationTest extends CreationTestTemplate
        <Pair<Triplet<Boolean, Character, LocalDate>, Map<Integer, Pair<Integer, Item<Integer>>>>> {

    @Override
    protected void verify(final Pair<Triplet<Boolean, Character, LocalDate>, Map<Integer, Pair<Integer, Item<Integer>>>> result) {
        assertThat(result.getLeft().getLeft()).isInstanceOf(Boolean.class);
        assertThat(result.getLeft().getMid()).isInstanceOf(Character.class);
        assertThat(result.getLeft().getRight()).isInstanceOf(LocalDate.class);

        assertThat(result.getRight()).isInstanceOf(Map.class);
        assertThat(result.getRight().entrySet()).isNotEmpty()
                .allSatisfy(entry -> {
                    assertThat(entry.getKey()).isInstanceOf(Integer.class);
                    assertThat(entry.getValue().getLeft()).isInstanceOf(Integer.class);
                    assertThat(entry.getValue().getRight().getValue()).isInstanceOf(Integer.class);
                });
    }
}
