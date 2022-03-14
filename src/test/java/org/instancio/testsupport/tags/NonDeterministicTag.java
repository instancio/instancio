package org.instancio.testsupport.tags;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * An annotation that indicates that a test makes assertions
 * on randomly generated values. While the intention is for the test to pass,
 * there is a very small chance it might fail.
 * <p>
 * For example asserting that a randomly generated {@code int} within
 * {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE} is not zero.
 */
@Tag("non-deterministic")
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NonDeterministicTag {

    String value() default "";
}
