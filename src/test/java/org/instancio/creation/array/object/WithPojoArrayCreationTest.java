package org.instancio.creation.array.object;

import org.instancio.pojo.arrays.object.WithPojoArray;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.instancio.testsupport.templates.ArrayCreationTestTemplate;
import org.instancio.testsupport.templates.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;

public class WithPojoArrayCreationTest extends ArrayCreationTestTemplate<WithPojoArray> {

    @Override
    @NonDeterministicTag
    @NumberOfExecutions
    protected void verify(WithPojoArray result) {
        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
