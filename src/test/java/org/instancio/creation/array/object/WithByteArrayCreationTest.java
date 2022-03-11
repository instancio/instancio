package org.instancio.creation.array.object;

import org.instancio.pojo.arrays.object.WithByteArray;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;

public class WithByteArrayCreationTest extends ArrayCreationTestTemplate<WithByteArray> {

    @Override
    @NonDeterministicTag
    @NumberOfExecutions(30)
    protected void verify(WithByteArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
