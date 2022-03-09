package org.instancio.creation.array.primitive;

import org.instancio.pojo.arrays.primitive.WithIntArray;
import org.instancio.testsupport.base.CreationTestTemplate;
import org.instancio.testsupport.base.NumberOfExecutions;
import org.instancio.testsupport.utils.ArrayUtils;
import org.junit.jupiter.api.AfterAll;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class WithIntArrayCreationTest extends CreationTestTemplate<WithIntArray> {

    private final Set<Integer> generatedValues = new HashSet<>();

    @AfterAll
    void verifyUniqueValues() {
        assertThat(generatedValues)
                .as("Expected distinct randomly generated values")
                .hasSizeGreaterThan(1);
    }

    @Override
    @NumberOfExecutions(10)
    protected void verify(WithIntArray result) {
        assertThat(result.getValues()).isNotNull().doesNotHaveDuplicates();

        generatedValues.addAll(ArrayUtils.toList(result.getValues()));
    }

}
