package org.instancio.pojo.collections;

import lombok.Getter;
import lombok.ToString;
import org.instancio.pojo.person.Person;

import java.util.Map;

@Getter
@ToString
public class StringPersonMap {
    private Map<String, Person> mapField;
}
