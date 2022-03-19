package org.instancio.creation.generics;

import org.instancio.pojo.generics.basic.Item;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.Map;

@GenericsTag
public class PairWithMapContainingPairCreationTest extends CreationTestTemplate
        <Pair<Integer, Map<String, Pair<Integer, Item<Integer>>>>> {

    @Override
    protected void verify(final Pair<Integer, Map<String, Pair<Integer, Item<Integer>>>> result) {
        // auto-verified
    }
}
