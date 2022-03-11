package org.instancio.creation.array.object;

import org.instancio.pojo.arrays.object.WithFloatArray;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;

public class WithFloatArrayCreationTest extends ArrayCreationTestTemplate<WithFloatArray> {

    @Override
    @NonDeterministicTag
    @NumberOfExecutions
    protected void verify(WithFloatArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
