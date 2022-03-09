package org.instancio.creation.array.primitive;

import org.instancio.pojo.arrays.primitive.WithDoubleArray;
import org.instancio.testsupport.annotations.NonDeterministic;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;

public class WithDoubleArrayCreationTest extends ArrayCreationTestTemplate<WithDoubleArray> {

    @Override
    @NonDeterministic
    @NumberOfExecutions
    protected void verify(WithDoubleArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
