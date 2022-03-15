package org.instancio.testsupport.tags;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Tag for tests verifying correctness of the model.
 */
@Tag("model")
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelTag {
}
