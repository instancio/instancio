package org.instancio.pojo.collections.maps;

import lombok.Getter;
import lombok.ToString;
import org.instancio.pojo.person.Person;

import java.util.Map;

@Getter
@ToString
public class MapStringPerson {

    private Map<String, Person> map;
}
