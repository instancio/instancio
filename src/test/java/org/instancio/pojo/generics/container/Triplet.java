package org.instancio.pojo.generics.container;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Triplet<M, N, O> {
    private M left;
    private N mid;
    private O right;

}
