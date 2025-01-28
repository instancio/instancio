package org.instancio.internal.selectors

import org.instancio.GetMethodSelector
import org.instancio.SetMethodSelector
import org.instancio.exception.InstancioApiException
import org.instancio.test.support.pojo.person.Person
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import java.util.stream.Stream

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

class MethodRefTest {
    private static Stream<Arguments> args() {
        final GetMethodSelector<Person, Integer> getter = Person::getAge
        final SetMethodSelector<Person, Integer> setter = Person::setAge

        return Stream.of(
                Arguments.of(getter, 'getAge'),
                Arguments.of(setter, 'setAge'))
    }

    @MethodSource('args')
    @ParameterizedTest
    void 'Should properly extract MethodRef'(final Serializable methodRef, final String expectedMethodName) {
        final MethodRef result = MethodRef.from(methodRef)

        assertThat(result.getTargetClass()).isEqualTo(Person.class)
        assertThat(result.getMethodName()).isEqualTo(expectedMethodName)
    }

    @Test
    void 'Should throw error when method cannot be resolved'() {
        final def closure = { Person p -> p.name }

        assertThatThrownBy(() -> MethodRef.from(closure))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining('unable to resolve method name from selector');
    }
}
