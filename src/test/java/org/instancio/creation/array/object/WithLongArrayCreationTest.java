package org.instancio.creation.array.object;

import org.instancio.pojo.arrays.object.WithLongArray;
import org.instancio.testsupport.annotations.NonDeterministic;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;

public class WithLongArrayCreationTest extends ArrayCreationTestTemplate<WithLongArray> {

    @Override
    @NonDeterministic
    @NumberOfExecutions
    protected void verify(WithLongArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
