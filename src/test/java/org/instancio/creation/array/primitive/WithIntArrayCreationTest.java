package org.instancio.creation.array.primitive;

import org.instancio.pojo.arrays.primitive.WithIntArray;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;

public class WithIntArrayCreationTest extends ArrayCreationTestTemplate<WithIntArray> {

    @Override
    @NonDeterministicTag
    @NumberOfExecutions
    protected void verify(WithIntArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
