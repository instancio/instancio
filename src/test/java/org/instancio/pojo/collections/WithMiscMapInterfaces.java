package org.instancio.pojo.collections;

import lombok.Getter;
import lombok.ToString;
import org.instancio.pojo.person.Person;

import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentNavigableMap;

@Getter
@ToString
public class WithMiscMapInterfaces {

    private SortedMap<Integer, String> sortedMap;
    private NavigableMap<String, Person> navigableMap;
    private ConcurrentNavigableMap<String, Person> concurrentNavigableMap;

}
