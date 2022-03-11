package org.instancio.creation.array.object;

import org.instancio.pojo.arrays.primitive.WithBooleanArray;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;

public class WithBooleanArrayCreationTest extends ArrayCreationTestTemplate<WithBooleanArray> {

    @Override
    protected int minNumberOfGeneratedValues() {
        return 2;
    }

    @Override
    @NonDeterministicTag
    @NumberOfExecutions(20)
    protected void verify(WithBooleanArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
