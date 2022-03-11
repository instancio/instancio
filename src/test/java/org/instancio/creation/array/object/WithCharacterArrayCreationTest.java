package org.instancio.creation.array.object;

import org.instancio.pojo.arrays.object.WithCharacterArray;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;

public class WithCharacterArrayCreationTest extends ArrayCreationTestTemplate<WithCharacterArray> {

    @Override
    protected int minNumberOfGeneratedValues() {
        return 10;
    }

    @Override
    @NonDeterministicTag
    @NumberOfExecutions(50)
    protected void verify(WithCharacterArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
