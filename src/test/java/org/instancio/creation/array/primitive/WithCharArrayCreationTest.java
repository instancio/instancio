package org.instancio.creation.array.primitive;

import org.instancio.pojo.arrays.primitive.WithCharArray;
import org.instancio.testsupport.annotations.NonDeterministic;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;

public class WithCharArrayCreationTest extends ArrayCreationTestTemplate<WithCharArray> {

    @Override
    protected int minNumberOfGeneratedValues() {
        return 10;
    }

    @Override
    @NonDeterministic
    @NumberOfExecutions(50)
    protected void verify(WithCharArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
