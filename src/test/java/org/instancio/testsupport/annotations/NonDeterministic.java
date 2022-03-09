package org.instancio.testsupport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * An informational annotation that Indicates that a test makes assertions
 * on randomly generated values. While the intention is for the test to pass,
 * there is a very small chance it might fail.
 * <p>
 * For example asserting that a randomly generated {@code int} within
 * {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE} is not zero.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NonDeterministic {
}
