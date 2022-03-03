package org.instancio.pojo.maps;

import lombok.Getter;
import lombok.ToString;
import org.instancio.pojo.person.Person;

import java.util.Map;

public class WithMap {

    @Getter
    @ToString
    public static class IntegerString {
        private Map<Integer, String> map;
    }

    @Getter
    @ToString
    public static class StringPerson {
        private Map<String, Person> map;
    }

}
