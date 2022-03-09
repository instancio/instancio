package org.instancio.testsupport.tags;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Tag for tests verifying objects created via the API.
 */
@Tag("create")
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface CreateTag {
}
