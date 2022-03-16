package org.instancio.pojo.generics;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ListWithoutType {

    @SuppressWarnings("rawtypes")
    private List list; // type intentionally not specified
}
