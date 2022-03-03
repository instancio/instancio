package org.instancio.pojo.sets;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

@Getter
@Setter
@ToString
public class WithSet {

    private Set<String> set;
    private SortedSet<Long> sortedSet;
    private NavigableSet<Integer> navigableSet;
    private HashSet<String> hashSet;
    private TreeSet<Long> treeSet;
    private ConcurrentSkipListSet<Integer> concurrentSkipListSet;
}
