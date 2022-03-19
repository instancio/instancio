package org.instancio.testsupport.tags;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Tag for tests verifying behaviour in the presence of circular references.
 */
@Tag("cyclic")
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface CyclicTag {
}
