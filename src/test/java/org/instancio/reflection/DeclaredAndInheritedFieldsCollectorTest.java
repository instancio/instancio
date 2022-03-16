package org.instancio.reflection;

import org.instancio.pojo.inheritance.BaseClasSubClassInheritance;
import org.instancio.pojo.person.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DeclaredAndInheritedFieldsCollectorTest {

    private final FieldCollector fieldsCollector = new DeclaredAndInheritedFieldsCollector();

    @Test
    void getFields() {
        final List<Field> results = fieldsCollector.getFields(BaseClasSubClassInheritance.SubClass.class);
        assertThat(results)
                .extracting(Field::getName)
                .containsExactlyInAnyOrder(
                        "privateBaseClassField",
                        "protectedBaseClassField",
                        "subClassField");
    }

    @ValueSource(classes = {Object.class, int.class, String.class, List.class, ArrayList.class, LocalDate.class, Person[].class})
    @ParameterizedTest
    void getFieldsReturnsEmptyResults(Class<?> klass) {
        final List<Field> results = fieldsCollector.getFields(klass);
        assertThat(results).isEmpty();
    }

}
