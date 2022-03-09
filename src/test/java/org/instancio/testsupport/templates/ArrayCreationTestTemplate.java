package org.instancio.testsupport.templates;

import org.instancio.testsupport.Constants;
import org.junit.jupiter.api.AfterAll;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class ArrayCreationTestTemplate<T> extends CreationTestTemplate<T> {

    protected final Set<Object> generatedValues = new HashSet<>();

    protected int minNumberOfGeneratedValues() {
        return NumberOfExecutions.DEFAULT_VALUE * Constants.ARRAY_SIZE;
    }

    @AfterAll
    void verifyUniqueValues() {
        assertThat(generatedValues)
                .as("Expected distinct randomly generated values")
                .hasSizeGreaterThanOrEqualTo(minNumberOfGeneratedValues());
    }
}
