package org.instancio.creation.array.object;

import org.instancio.pojo.arrays.object.WithStringArray;
import org.instancio.testsupport.annotations.NonDeterministic;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;

public class WithStringArrayCreationTest extends ArrayCreationTestTemplate<WithStringArray> {

    @Override
    @NonDeterministic
    @NumberOfExecutions
    protected void verify(WithStringArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
