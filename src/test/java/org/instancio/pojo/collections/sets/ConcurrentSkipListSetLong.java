package org.instancio.pojo.collections.sets;

import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.ConcurrentSkipListSet;

@Getter
@ToString
public class ConcurrentSkipListSetLong {

    private ConcurrentSkipListSet<Long> set;
}
