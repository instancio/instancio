package org.instancio.pojo.circular;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class CircularList {

    // TODO test
    List<CircularList> list;

}
