package org.instancio.creation.array.primitive;

import org.instancio.pojo.arrays.primitive.WithByteArray;
import org.instancio.testsupport.annotations.NonDeterministic;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;

public class WithByteArrayCreationTest extends ArrayCreationTestTemplate<WithByteArray> {

    @Override
    @NonDeterministic
    @NumberOfExecutions(30)
    protected void verify(WithByteArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
