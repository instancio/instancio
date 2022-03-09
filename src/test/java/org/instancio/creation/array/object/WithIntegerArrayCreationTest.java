package org.instancio.creation.array.object;

import org.instancio.pojo.arrays.object.WithIntegerArray;
import org.instancio.testsupport.annotations.NonDeterministic;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;

public class WithIntegerArrayCreationTest extends ArrayCreationTestTemplate<WithIntegerArray> {

    @Override
    @NonDeterministic
    @NumberOfExecutions
    protected void verify(WithIntegerArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
