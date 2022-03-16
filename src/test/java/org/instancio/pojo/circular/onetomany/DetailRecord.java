package org.instancio.pojo.circular.onetomany;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DetailRecord {
    private Long id;
    private MainRecord mainRecord;
}
