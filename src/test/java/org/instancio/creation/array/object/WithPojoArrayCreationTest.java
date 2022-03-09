package org.instancio.creation.array.object;

import org.instancio.pojo.arrays.object.WithPojoArray;
import org.instancio.testsupport.annotations.NonDeterministic;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;

public class WithPojoArrayCreationTest extends ArrayCreationTestTemplate<WithPojoArray> {

    @Override
    @NonDeterministic
    @NumberOfExecutions
    protected void verify(WithPojoArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
