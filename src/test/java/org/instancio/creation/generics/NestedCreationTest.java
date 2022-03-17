package org.instancio.creation.generics;

import org.instancio.pojo.generics.basic.Item;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

//public class NestedCreationTest extends CreationTestTemplate
//        <Pair<Triplet<Boolean, Character, LocalDate>,
//                Map<Integer, Pair<Integer, Item<Integer>>>>> {
@GenericsTag
public class NestedCreationTest extends CreationTestTemplate<Pair<Item<String>, Integer>> {

    @Override
    protected void verify(Pair<Item<String>, Integer> result) {
        System.out.println(result);
    }
}
