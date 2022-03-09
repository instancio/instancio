package org.instancio.creation.array.object;

import org.instancio.pojo.arrays.object.WithEnumArray;
import org.instancio.testsupport.annotations.NonDeterministic;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;

public class WithEnumArrayCreationTest extends ArrayCreationTestTemplate<WithEnumArray> {

    @Override
    protected int minNumberOfGeneratedValues() {
        return WithEnumArray.NumberEnum.values().length;
    }

    @Override
    @NonDeterministic
    @NumberOfExecutions(20)
    protected void verify(WithEnumArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
