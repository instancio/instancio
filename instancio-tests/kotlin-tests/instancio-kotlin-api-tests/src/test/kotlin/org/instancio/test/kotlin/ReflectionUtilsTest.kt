package org.instancio.test.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.instancio.junit.internal.ReflectionUtils
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

class ReflectionUtilsTest {
    @Test
    fun collectionAnnotations() {
        val annotations = ReflectionUtils.collectionAnnotations(AnnotatedClass::class.java)

        assertThat(annotations).hasSize(1)
            .extracting<KClass<*>> { it.annotationClass }
            .contains(AnnotationY::class)
    }

    @AnnotationY
    @Retention(AnnotationRetention.RUNTIME)
    private annotation class AnnotationY

    @AnnotationY
    private class AnnotatedClass
}
