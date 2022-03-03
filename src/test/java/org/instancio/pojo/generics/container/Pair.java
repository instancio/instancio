package org.instancio.pojo.generics.container;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Pair<L, R> {
    private L left;
    private R right;

}
